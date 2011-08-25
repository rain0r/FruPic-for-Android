package net.frubar.frupic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class FruPic extends Activity implements Runnable, OnClickListener {
	private static final String TAG = "FruPic";
	
	protected String username = "";
	protected ProgressDialog pd;
	protected Handler progressHandler;
	protected byte[] imageData = { 0 };
	protected Handler mHandler = new Handler();
	
	public static int maximum = 0;
	public static int increment = 0;
	public static String imageURL = "";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Log.d(TAG, "onCreate()");

		// retrieve the username 
		// Log.d(TAG, "getUsername");
		this.username = Prefs.getUsername(this);
		
		// welcome the user
		// Log.d(TAG, "find Hello View");
		TextView tvHello = (TextView) findViewById(R.id.hello);
		tvHello.setText( String.format(getString(R.string.hello), this.username ) );	
		
		// context stuff for the gallery
		// Log.d(TAG, "getIntent()");
		Intent intent = getIntent();
		
		// Log.d(TAG, "getAction()");
		String action = intent.getAction();
		
		// Log.d(TAG, "getExtras()");
		Bundle extras = intent.getExtras();
		
		// if this is from the share menu
		if (Intent.ACTION_SEND.equals(action)) {
			
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				// Log.d(TAG, "Context Menu");
				
				// Get resource path from intent callee
				Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);										
				
				// Query gallery for camera picture via
				// Android ContentResolver interface
				ContentResolver cr = getContentResolver();
				
				// dialog
				this.initDialog();
				
				try {
					InputStream is = cr.openInputStream(uri);
					
					// Get binary bytes for encode
					this.imageData = getBytesFromFile(is);
				}
				catch (Exception e) {
					// Log.e(TAG, "Exception" , e);
				}        
				
				// maximum = bytes of the image
				this.maximum = this.imageData.length;
				// Log.d(TAG, "the length of the byte array: "+this.maximum);
				
				// upload the image
				/*
				// Log.d(TAG, "calling uploadImage()");
				try {
					Upload u = new Upload(this);
					Thread t = new Thread(u.uploadImage());
					
					while (FruPic.increment < FruPic.maximum) {
						t.start();
					}
				}
				catch (Exception e) {
					// Log.e(TAG, "Exception" , e);
				}
				*/
				
				// Log.d(TAG, "Starting the thread");
				Thread thread = new Thread(this);
				thread.start();
				
				return;
			}	
		}
	}

	private void initDialog() {
		this.pd = new ProgressDialog(this);
		this.pd.setCancelable(true);
		this.pd.setMessage("Uploading...");
		this.pd.setIndeterminate(true);
        
        // set the progress to be horizontal
		// this.pd.setProgressStyle(ProgressDia// Log.STYLE_HORIZONTAL);
        
        // reset the bar to the default value of 0
		// this.pd.setProgress(0);
        
        // set the maximum value
		// this.pd.setMax(FruPic.maximum);
        
        // display the progressbar
		this.pd.show();
	}
	
	// reading a file byte by byte
	public static byte[] getBytesFromFile(InputStream is) {
		// Log.d(TAG, "getBytesFromFile()");
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			return buffer.toByteArray();
		} catch (IOException e) {
			// Log.e(TAG, "Exception" , e);
			return null;
		}
	}
	
	// display menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}	
	
	// selected a menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// by clicking on the settings item
			case R.id.menu_settings:
				// Log.d(TAG, "Starting Prefs()");
				startActivity(new Intent(this, Prefs.class));
				return true;

			// by clicking on the about item
			case R.id.menu_about:
				// Log.d(TAG, "Starting About()");
				startActivity(new Intent(this, About.class));
				return true;
		}
		return false;
	   }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Upload u = new Upload(this);
		// Log.d(TAG, "calling uploadImage()");
		u.uploadImage();
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Log.d(TAG, "dismiss the progress dialog");
			pd.dismiss();

			// upload done!
			// Log.d(TAG, "display some nice text");
			TextView tvUploadDone = (TextView) findViewById(R.id.uploadDone);
			tvUploadDone.setText(R.string.upload_done_image_url);
			
			// hide the welcome text
			TextView tvWelcomeText = (TextView) findViewById(R.id.welcome_text);
			tvWelcomeText.setVisibility(TextView.INVISIBLE);
			
			// display the image url
			TextView tvImageUrl = (TextView) findViewById(R.id.imageURL);
			tvImageUrl.setText(
		            Html.fromHtml( "<a href=\""+FruPic.imageURL+"\">"+FruPic.imageURL+"</a>" ) 
			);
			tvImageUrl.setMovementMethod(LinkMovementMethod.getInstance());
		}
	};
}
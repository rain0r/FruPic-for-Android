package net.frubar.frupic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FruPic extends Activity implements OnClickListener {
	private static final String TAG = "FruPic";
	final private String FruPicApi = "http://api.freamware.net/2.0/upload.picture";
	private static String imageURL = "";
	private String username = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d(TAG, "onCreate()");

		// retrieve the username 
		username = Prefs.getUsername(this);
		
		// welcome the user
		TextView tvHello = (TextView) findViewById(R.id.hello);
		tvHello.setText( String.format(getString(R.string.hello), username ) );			

		
		// context stuff for the gallery
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();

		// if this is from the share menu
		if (Intent.ACTION_SEND.equals(action)) {
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				try {
					// Get resource path from intent callee
					Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

					// Query gallery for camera picture via
					// Android ContentResolver interface
					ContentResolver cr = getContentResolver();
					InputStream is = cr.openInputStream(uri);
					
					// Get binary bytes for encode
					byte[] data = getBytesFromFile(is);
					
					// upload the image
					Log.d(TAG, "calling uploadImage()");
					uploadImage(data);
					
					// upload done!
					Log.d(TAG, "display some nice text");
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

					return;
				} catch (Exception e) {
					Log.e(TAG, "Exception" , e);
				}

			} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
				return;
			}
		}
	}

	// the actual upload process
	private String uploadImage(byte[] data) {
		Log.d(TAG, "SendRequest()");
		
	    HttpURLConnection conn = null;
	    DataInputStream inStream = null; 
	    DataOutputStream dos = null;
	    
	    String lineEnd = "\r\n";
	    String twoHyphens = "--";
	    String boundary =  "ForeverFrubarIWantToBe";

		try {
			URL url = new URL(this.FruPicApi);			
            conn = (HttpURLConnection) url.openConnection();
			
			// Create socket
            Log.d(TAG, "creating Socket");
            conn.setDoInput(true);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            
            // prepare output
            conn.setDoOutput(true);
            conn.setUseCaches(false);
 
            // begin the header
            Log.d(TAG, "beginning with header");
            conn.setRequestMethod("POST");
            
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
 
            dos = new DataOutputStream( conn.getOutputStream() );
            
            // Tags
            dos.writeBytes(lineEnd+twoHyphens+boundary+lineEnd);
            dos.writeBytes("Content-Disposition: form-data;name='tags';");
            dos.writeBytes(lineEnd+lineEnd+"via:Android;"+lineEnd+lineEnd+twoHyphens+boundary+lineEnd);
            
            // Username
            if(!username.equals("")) {
	            dos.writeBytes(lineEnd+twoHyphens+boundary+lineEnd);
	            dos.writeBytes("Content-Disposition: form-data;name='username';");
	            dos.writeBytes(lineEnd+lineEnd+username+";"+lineEnd+lineEnd+twoHyphens+boundary+lineEnd);
            }
            
            dos.writeBytes("Content-Disposition: form-data;"+"name='file';"+"filename='frup0rn.png'"+lineEnd);
            dos.writeBytes(lineEnd);

            // sending the image byte by byte
            Log.d(TAG, "STARTING sending the image");
            for(byte var : data) {
            	dos.writeByte(var);
            }
            Log.d(TAG, "FINISHED sending the image");
            
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
 
            // close the steram
            dos.flush();
            dos.close();   
		}
        catch (MalformedURLException ex)
        {
        	Log.e(TAG, "Exception" , ex);
        }
        catch (IOException ioe)
        {
        	Log.e(TAG, "Exception" , ioe);
        }
        
        // Reading Headers
        Log.d(TAG, "trying to read the header");
        Log.d(TAG, "===== HEADER =====");
        try {
        	for (int i=0; ; i++) {
        		String name = conn.getHeaderFieldKey(i);
        		String value = conn.getHeaderField(i);
        		if (name == null && value == null) {
        			break;         
        		}
        		if (name == null){
        			Log.d(TAG, "Server HTTP version, Response code:");
        			Log.d(TAG, value);
        			Log.d(TAG, "\n");
        		}
        		else{
        			Log.d(TAG, name + "="+ value);
        		}
        	}
        } 
        catch (Exception e) {
        	Log.e(TAG, "Exception" , e);
        }      
        Log.d(TAG, "===== HEADER =====");
        
        // listening to the Server Response
        Log.d(TAG, "listening to the server");
        try
        {        	
            inStream = new DataInputStream ( conn.getInputStream() );
      
            String str = "";
            String output = "";
 
            while (( str = inStream.readLine()) != null)
            {
            	output = output+str;
            	Log.d(TAG, output);
            	
            	// save the url to the image
            	FruPic.imageURL = output;
            	Log.d(TAG, "the image url is: "+FruPic.imageURL);
            }
            inStream.close();
        }
        catch (IOException ioex)
        {
        	Log.e(TAG, "Exception" , ioex);
        }
        
        return FruPic.imageURL;
	}

	// reading a file byte by byte
	public static byte[] getBytesFromFile(InputStream is) {
		Log.d(TAG, "getBytesFromFile()");
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
			Log.e(TAG, "Exception" , e);
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
				Log.d(TAG, "Starting Prefs()");
				startActivity(new Intent(this, Prefs.class));
				return true;

			// by clicking on the about item
			case R.id.menu_about:
				Log.d(TAG, "Starting About()");
				startActivity(new Intent(this, About.class));
				return true;
		}
		return false;
	   }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
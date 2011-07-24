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
import android.widget.TextView;

public class FruPic extends Activity {
	private static final String TAG = "FruPic";
	final private String FruPicApi = "http://api.freamware.net/2.0/upload.picture";
	private static String imageURL = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d(TAG, "onCreate()");
		
		// context foobar
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

					Log.d(TAG, "calling uploadImage()");
					uploadImage(data);
					
					Log.d(TAG, "display some nice text");
					// display some nice text
					TextView tvUploadDone = (TextView) findViewById(R.id.uploadDone);
					tvUploadDone.setText(R.string.upload_done_image_url);
					
					// display the image url
					TextView tvImageUrl = (TextView) findViewById(R.id.imageURL);
					tvImageUrl.setText(
				            Html.fromHtml( "<a href=\""+FruPic.imageURL+"\">"+FruPic.imageURL+"</a>" ) 
							// FruPic.imageURL
					);
					tvImageUrl.setMovementMethod(LinkMovementMethod.getInstance());


					return;
				} catch (Exception e) {
					Log.d(TAG, ">> Exception: " + e.getMessage());
				}

			} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
				return;
			}
		}
	}

	private String uploadImage(byte[] data) {
		Log.d(TAG, "SendRequest()");
		
	    HttpURLConnection conn = null;
	    DataInputStream inStream = null; 
	    DataOutputStream dos = null;
	    
	    String lineEnd = "\r\n";
	    String twoHyphens = "--";
	    String boundary =  "ForeverFrubarIWantToBe";

		try {
			Log.d(TAG, "Trying...");
			URL url = new URL(this.FruPicApi);			
            conn = (HttpURLConnection) url.openConnection();

			
			// Create socket
            Log.d(TAG, "creating Socket");
            conn.setDoInput(true);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            
            //Output empfangen
            conn.setDoOutput(true);
            conn.setUseCaches(false);
 
            //Verbindungseinstellungen
            Log.d(TAG, "beginning with header");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
 
            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name='file'; filename='"+ "frup0rn.png"+"'"+ lineEnd);
            dos.writeBytes(lineEnd);

            // sending the image byte by byte
            Log.d(TAG, "STARTING sending the image");
            for(byte var : data) {
            	// Log.d(TAG, "sending a byte");
            	dos.writeByte(var);
            }
            Log.d(TAG, "FINISHED sending the image");
            
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
 
            //Stream schliessen

            dos.flush();
            dos.close();   
		}
        catch (MalformedURLException ex)
        {
        	Log.e(this.getClass().getName(), ex.toString());
        }
        catch (IOException ioe)
        {
        	Log.e(this.getClass().getName(), ioe.toString());
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
        	Log.d(TAG, ">> Exception: " + e.getMessage());
        }      
        Log.d(TAG, "===== HEADER =====");
        
        // listening to the Server Response
        Log.d(TAG, "listening to the server");
        try
        {        	
            inStream = new DataInputStream ( conn.getInputStream() );
      
            String str;
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
        	Log.d(TAG, ">> Exception: " + ioex.getMessage());
        }
        
        return FruPic.imageURL;
	}

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
			Log.d(TAG, ">> Exception: " + e.getMessage());
			return null;
		}
	}
}
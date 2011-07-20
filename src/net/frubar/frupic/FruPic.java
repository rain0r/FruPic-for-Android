package net.frubar.frupic;

import java.io.ByteArrayOutputStream;
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
import android.util.Log;

public class FruPic extends Activity {
	private static final String TAG = "FruPic";
	final private String FruPicApi = "http://api.freamware.net/2.0/upload.picture";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d(TAG, ">> onCreate()");

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

					Log.d(TAG, ">> calling SendRequest()");
					SendRequest(data);

					return;
				} catch (Exception e) {
					Log.e(this.getClass().getName(), e.toString());
					Log.e(TAG, ">> " + e.toString());
				}

			} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
				return;
			}
		}
	}

	private void SendRequest(byte[] data) {
		Log.d(TAG, ">> SendRequest()");
		
	    HttpURLConnection conn = null;
	    DataOutputStream dos = null;
	    String lineEnd = "\r\n";
	    String twoHyphens = "--";
	    String boundary =  "ForeverFrubarIWantToBe";

		try {
			URL url = new URL(this.FruPicApi);			
            conn = (HttpURLConnection) url.openConnection();

			
			// Create socket
            conn.setDoInput(true);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            
            //Output empfangen
            conn.setDoOutput(true);
            conn.setUseCaches(false);
 
            //Verbindungseinstellungen
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
 
            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name='file'; filename='" + "frup0rn.png" +"'" + lineEnd);
            dos.writeBytes(lineEnd);
           
            for(byte var : data) {
            	dos.writeByte(var);
            }
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
        try 
        {            
          for (int i=0; ; i++) 
          {
                String name = conn.getHeaderFieldKey(i);
                String value = conn.getHeaderField(i);
                if (name == null && value == null){
                  break;         
                }
                if (name == null){
                	Log.d(TAG, "Server HTTP version, Response code:");
                	Log.d(TAG, value);
                	Log.d(TAG, "\n");
                }
                else{
                	Log.d(TAG, name + "=" + value);
                }
          }
        } 
        catch (Exception e) {
        	Log.e(this.getClass().getName(), e.toString());
        }      
	}


	public static byte[] getBytesFromFile(InputStream is) {
		Log.d(TAG, ">> getBytesFromFile()");
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
			Log.e(TAG, e.toString());
			return null;
		}
	}

}
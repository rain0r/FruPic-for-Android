package net.frubar.frupic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Upload {
	private static final String TAG = "Upload";
	private final String FruPicApi = "http://api.freamware.net/2.0/upload.picture";
	private FruPic fru;
	
	public Upload(FruPic _fru) {
		this.fru = _fru;
	}

	public void uploadImage() {
		// Log.d(TAG, "SendRequest()");

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
            // Log.d(TAG, "creating Socket");
            conn.setDoInput(true);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            
            // prepare output
            conn.setDoOutput(true);
            conn.setUseCaches(false);
 
            // begin the header
            // Log.d(TAG, "beginning with header");
            conn.setRequestMethod("POST");
            
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
 
            dos = new DataOutputStream( conn.getOutputStream() );
            
            // Tags
            dos.writeBytes(lineEnd+twoHyphens+boundary+lineEnd);
            dos.writeBytes("Content-Disposition: form-data;name='tags';");
            dos.writeBytes(lineEnd+lineEnd+"via:Android;"+lineEnd+lineEnd+twoHyphens+boundary+lineEnd);
            
            // Username
            if (!fru.username.equals("")) {
	            dos.writeBytes(lineEnd+twoHyphens+boundary+lineEnd);
	            dos.writeBytes("Content-Disposition: form-data;name='username';");
	            dos.writeBytes(lineEnd+lineEnd+fru.username+";"+lineEnd+lineEnd+twoHyphens+boundary+lineEnd);
            }
            
            dos.writeBytes("Content-Disposition: form-data;"+"name='file';"+"filename='frup0rn.png'"+lineEnd);
            dos.writeBytes(lineEnd);

            // sending the image byte by byte
            // Log.d(TAG, "STARTING sending the image");
            // Log.d(TAG, "Length of imageData: "+fru.imageData.length);
            
            // the actual upload process
            for (byte var : fru.imageData) {
            	dos.writeByte(var);
            	FruPic.increment++;
            	
            	if (FruPic.increment % 1024 == 0) {
            		// Log.d(TAG, FruPic.increment+" so far");
            		// fru.pd.setProgress(FruPic.increment);
            	}
            }

            // Log.d(TAG, "FINISHED sending the image");
            
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
 
            // close the stream
            // Log.d(TAG, "closing the stream");
            dos.flush();
            dos.close();
		}
        catch (MalformedURLException ex)
        {
        	// Log.e(TAG, "Exception" , ex);
        }
        catch (IOException ioe)
        {
        	// Log.e(TAG, "Exception" , ioe);
        }
        
        // Reading Headers
        // Log.d(TAG, "trying to read the header");
        // Log.d(TAG, "===== HEADER =====");
        try {
        	for (int i=0; ; i++) {
        		String name = conn.getHeaderFieldKey(i);
        		String value = conn.getHeaderField(i);
        		
        		if (name == null && value == null) {
        			break;         
        		}
        		
        		if (name == null){
        			// Log.d(TAG, "Server HTTP version, Response code:");
        			// Log.d(TAG, value);
        			// Log.d(TAG, "\n");
        		}
        		else {
        			// Log.d(TAG, name + "="+ value);
        		}
        	}
        } 
        catch (Exception e) {
        	// Log.e(TAG, "Exception" , e);
        }      
        // Log.d(TAG, "===== HEADER =====");
        
        // listening to the Server Response
        // Log.d(TAG, "listening to the server");
        try {        	
            inStream = new DataInputStream ( conn.getInputStream() );
      
            String str = "";
            String output = "";
 
            while (( str = inStream.readLine()) != null)
            {
            	output = output+str;
            	// Log.d(TAG, output);
            	
            	// save the url to the image
            	FruPic.imageURL = output;
            	// Log.d(TAG, "the image url is: "+FruPic.imageURL);
            }
            inStream.close();
        }
        catch (IOException ioex)
        {
        	// Log.e(TAG, "Exception" , ioex);
        }
        
        return;
	}
}

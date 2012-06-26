package com.rec.photoeditor.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class ImageScannerAdapter implements MediaScannerConnectionClient {
	
	Context context;
	String path;
	MediaScannerConnection conn;

	public ImageScannerAdapter(Context c) {
		this.context = c;
	}
	
	public void scanImage(String path) 
	{ 
		this.path = path;
		
	    if(conn!=null) conn.disconnect();  
	    conn = new MediaScannerConnection(context, this); 
	    conn.connect(); 
	}

	public void onMediaScannerConnected() { 
	    try{
	    	Log.i("REC Photo Editor","Start Media Scanner");
	        conn.scanFile(path, "image/*");
	       } catch (java.lang.IllegalStateException e){
	       }
	}

	public void onScanCompleted(String path, Uri uri) { 
	    conn.disconnect(); 
	} 
}

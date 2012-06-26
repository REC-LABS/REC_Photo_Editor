package com.rec.photoeditor.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Utility class to open ACTION_SEND intent with default values.
 */
public class SendMailUtil {
	public static void sendIntent(String imgName, Context c) {
		
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
	    sendIntent.setType("image/jpeg");
	    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Photo from REC Photo Editor");
	    sendIntent.putExtra(Intent.EXTRA_EMAIL, "my@emial.com");
	    sendIntent.putExtra(Intent.EXTRA_STREAM,  Uri.fromFile(new File(imgName)));
	    sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the photo");
	    c.startActivity(Intent.createChooser(sendIntent, "Email:"));
	    
	    
	    
	}
}

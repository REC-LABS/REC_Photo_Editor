package com.rec.photoeditor;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.rec.photoeditor.graphics.ImageProcessor;

import android.app.Application;

public class Platform extends Application {
	public static Facebook facebook;
	public static AsyncFacebookRunner asyncFbRunner;

	public static String userUID;
	public static String[] facebookPermissions = { 
		"offline_access",
		"publish_stream", 
//		"user_photos", 
		"photo_upload" };

	@Override
	public void onCreate() {
		super.onCreate();
		ImageProcessor.getInstance().setApplicationCotnext(
				getApplicationContext());
	}
}

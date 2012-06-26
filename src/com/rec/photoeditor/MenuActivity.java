package com.rec.photoeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MenuActivity extends Activity implements OnClickListener {
	private final static int ACTIVITY_PICK_IMAGE = 0;
	private final static int ACTIVITY_TAKE_PHOTO = 1;

	private ImageButton galleryButton;
	private ImageButton cameraButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		galleryButton = (ImageButton) findViewById(R.id.choose_from_sd_button);
		galleryButton.setOnClickListener(this);
		cameraButton = (ImageButton) findViewById(R.id.take_picture_button);
		cameraButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_from_sd_button:
			openGalleryButtonClicked();
			break;
		case R.id.take_picture_button:
			takePictureButtonClicked();
			break;
		default:
			break;
		}
	}

	private void openGalleryButtonClicked() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, ACTIVITY_PICK_IMAGE);
	}

	private void takePictureButtonClicked() {
		Uri imageUri = Uri.fromFile(getTempFile(getApplicationContext()));
		Intent intent = createIntentForCamera(imageUri);
		startActivityForResult(intent, ACTIVITY_TAKE_PHOTO);
	}

	private File getTempFile(Context context) {
		String fileName = "temp_photo.jpg";
		File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, fileName);
	}

	private Intent createIntentForCamera(Uri imageUri) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTIVITY_PICK_IMAGE:
				startEditorActivity(data.toUri(0));
				break;
			case ACTIVITY_TAKE_PHOTO:
				// taken photo is in sent url, data is null
				Uri uri = Uri.fromFile(getTempFile(getApplicationContext()));

				startEditorActivity(uri.toString());
				break;
			default:
				break;
			}
		}
	}

	private void startEditorActivity(String url) {
		Intent i = new Intent(this, EditorActivity.class);
		i.putExtra(getString(R.string.image_uri_flag), url);
		startActivity(i);
	}
	
	public boolean hasImageCaptureBug() {

	    // list of known devices that have the bug
	    ArrayList<String> devices = new ArrayList<String>();
	    devices.add("android-devphone1/dream_devphone/dream");
	    devices.add("generic/sdk/generic");
	    devices.add("vodafone/vfpioneer/sapphire");
	    devices.add("tmobile/kila/dream");
	    devices.add("verizon/voles/sholes");
	    devices.add("google_ion/google_ion/sapphire");

	    return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
	            + android.os.Build.DEVICE);

	}
}

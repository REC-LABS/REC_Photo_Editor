package com.rec.photoeditor;

import static com.rec.photoeditor.editoractivity.EditorSaveConstants.RESTORE_SAVED_BITMAP;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.rec.photoeditor.editoractivity.BrightnessActivity;
import com.rec.photoeditor.editoractivity.CropActivity;
import com.rec.photoeditor.editoractivity.FilterActivity;
import com.rec.photoeditor.editoractivity.RotateActivity;
import com.rec.photoeditor.graphics.ImageProcessor;
import com.rec.photoeditor.graphics.ImageProcessorListener;
import com.rec.photoeditor.graphics.commands.RotateCommand;
import com.rec.photoeditor.share.ShareDialog;
import com.rec.photoeditor.utils.BitmapScalingUtil;
import com.rec.photoeditor.utils.ImageScannerAdapter;
import com.rec.photoeditor.utils.SaveToStorageUtil;
import com.rec.photoeditor.utils.SendMailUtil;

public class EditorActivity extends Activity implements OnClickListener
		 {


	private static final int EDITOR_FUNCTION = 1;
	private static final int AUTHORIZE_FACEBOOK = 2;

	private ImageView imageView;

	// Top bar buttons
	private ImageButton brightnessButton;
	private ImageButton cropButton;
	private ImageButton rotateButton;
	private ImageButton filtersButton;

	// Bottom bar buttons
	private ImageButton backButton;
	private ImageButton emailButton;
	private ImageButton shareButton;
	private ImageButton saveButton;

	private String savedImagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		initComponents();
		initImageView();
	}

	private void initComponents() {
		brightnessButton = (ImageButton) findViewById(R.id.brightness_button);
		brightnessButton.setOnClickListener(this);
		cropButton = (ImageButton) findViewById(R.id.crop_button);
		cropButton.setOnClickListener(this);
		rotateButton = (ImageButton) findViewById(R.id.rotate_button);
		rotateButton.setOnClickListener(this);
		filtersButton = (ImageButton) findViewById(R.id.filters_button);
		filtersButton.setOnClickListener(this);
		backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(this);
		emailButton = (ImageButton) findViewById(R.id.email_button);
		emailButton.setOnClickListener(this);
		shareButton = (ImageButton) findViewById(R.id.share_button);
		shareButton.setOnClickListener(this);
		saveButton = (ImageButton) findViewById(R.id.save_button);
		saveButton.setOnClickListener(this);
	}

	private void initImageView() {
		String imageUri = getIntent().getStringExtra(
				getString(R.string.image_uri_flag));
		Log.i("REC Photo Editor", "Image URI = " + imageUri);
		imageView = (ImageView) findViewById(R.id.image_view);

		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			openBitmap(imageUri);
		} else {
			restoreBitmap();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Platform.facebook != null) {
			if (Platform.facebook.isSessionValid()) {
				Platform.facebook.extendAccessTokenIfNeeded(this, null);
			}
		}
	}

	private void restoreBitmap() {
		Log.i("Photo Editor", "Restore bitmap");
		Bitmap b = ImageProcessor.getInstance().getBitmap();
		if (b != null) {
			imageView.setImageBitmap(b);
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Bundle saveObject = new Bundle();
		saveObject.putInt("Bitmap", RESTORE_SAVED_BITMAP);
		return saveObject;
	}

	private void openBitmap(String imageUri) {
		Log.i("Photo Editor", "Open Bitmap");
		Bitmap b;
		try {
			b = BitmapScalingUtil.bitmapFromUri(this, Uri.parse(imageUri));
			if (b != null) {
				Log.i("REC Photo Editor", "Opened Bitmap Size: " + b.getWidth()
						+ " " + b.getHeight());
			}
			ImageProcessor.getInstance().setBitmap(b);
			imageView.setImageBitmap(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.brightness_button:
			brightnessButtonClicked();
			break;
		case R.id.crop_button:
			cropButtonClicked();
			break;
		case R.id.rotate_button:
			rotateButtonClicked();
			break;
		case R.id.filters_button:
			filtersButtonClicked();
			break;
		case R.id.back_button:
			backButtonClicked();
			break;
		case R.id.email_button:
			emailButtonClicked();
			break;
		case R.id.share_button:
			sharedButtonClicked();
			break;
		case R.id.save_button:
			saveButtonClicked();
			break;
		default:
			break;
		}
	}

	private void backButtonClicked() {
		finish();
	}

	private void emailButtonClicked() {

		if (!imageIsAlreadySaved() || ImageProcessor.getInstance().isModified()) {

			saveImage();
		}

		SendMailUtil.sendIntent(savedImagePath, this);
	}

	private boolean imageIsAlreadySaved() {
		return savedImagePath != null && !savedImagePath.equals("");
	}

	private void sharedButtonClicked() {
		if (!imageIsAlreadySaved() || ImageProcessor.getInstance().isModified()) {
			saveImage();
		}
		ShareDialog share = new ShareDialog(this, this, savedImagePath);
		share.show();
	}

	private void saveButtonClicked() {
		saveImage();
		Toast.makeText(this, R.string.photo_saved_info, Toast.LENGTH_LONG)
				.show();
	}

	private void saveImage() {
		savedImagePath = SaveToStorageUtil.save(ImageProcessor.getInstance()
				.getBitmap(), this);
		ImageScannerAdapter adapter = new ImageScannerAdapter(this);
		adapter.scanImage(savedImagePath);
		ImageProcessor.getInstance().resetModificationFlag();
	}

	private void deprotateButtonClicked() {
		
	}

	private void cropButtonClicked() {
		runEditorActivity(CropActivity.class);
	}

	private void brightnessButtonClicked() {
		runEditorActivity(BrightnessActivity.class);
	}

	private void filtersButtonClicked() {
		runEditorActivity(FilterActivity.class);
	}


	private void rotateButtonClicked() {
		runEditorActivity(RotateActivity.class);
	}

	private void brightnessButtonLongClick() {
		Toast.makeText(this, "Brightness long click", Toast.LENGTH_SHORT)
				.show();
	}

	private void runEditorActivity(Class<?> activityClass) {
		Intent i = new Intent(this, activityClass);
		startActivityForResult(i, EDITOR_FUNCTION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case AUTHORIZE_FACEBOOK:
			Platform.facebook.authorizeCallback(requestCode, resultCode, data);
			break;
		case EDITOR_FUNCTION:
			if (resultCode == RESULT_OK) {
				imageView.setImageBitmap(ImageProcessor.getInstance()
						.getBitmap());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.application_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.about_us_item:
			startAboutUsActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	

	private Runnable createPostRotateAction() {
		final Runnable postRotateAction = new Runnable() {
			public void run() {
				imageView.setImageBitmap(ImageProcessor.getInstance()
						.getBitmap());
				imageView.invalidate();
			}
		};
		return postRotateAction;
	}

	
	private void startAboutUsActivity() {
		Intent i = new Intent(this, AboutUsActivity.class);
		startActivity(i);
	}

}

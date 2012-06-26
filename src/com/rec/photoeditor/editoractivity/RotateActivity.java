package com.rec.photoeditor.editoractivity;

import static com.rec.photoeditor.editoractivity.EditorSaveConstants.RESTORE_PREVIEW_BITMAP;
import static com.rec.photoeditor.editoractivity.EditorSaveConstants.RESTORE_SAVED_BITMAP;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.rec.photoeditor.R;
import com.rec.photoeditor.graphics.ImageProcessor;
import com.rec.photoeditor.graphics.ImageProcessorListener;
import com.rec.photoeditor.graphics.commands.RotateCommand;

public class RotateActivity extends Activity implements ImageProcessorListener {
	private static final String TAG = "Photo Editor";
	private ImageProcessor imageProcessor;
	private ImageView imageView;
	private ImageButton okButton;
	private ImageButton cancelButton;
	private ImageButton rotateLeftButton;
	private ImageButton rotateRightButton;
	private SeekBar slider;
	private View progressBar;
	private int currentAngle=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rotate_editor);
		initializeComponents();
	}

	private void initializeComponents() {
		imageProcessor = ImageProcessor.getInstance();
		imageView = (ImageView) findViewById(R.id.image_view);
		slider = (SeekBar) findViewById(R.id.slider);
		okButton = (ImageButton) findViewById(R.id.ok_button);
		okButton.setOnClickListener(okButtonListener);
		cancelButton = (ImageButton) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(cancelButtonListener);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		initializeValues();	
		slider.setOnSeekBarChangeListener(sliderChangeListener);
		rotateLeftButton = (ImageButton) findViewById(R.id.rotate_left_button);
		rotateRightButton = (ImageButton) findViewById(R.id.rotate_right_button);
		rotateLeftButton.setOnClickListener(roteteLeftButtonListener);
		rotateRightButton.setOnClickListener(roteteRightButtonListener);
	}

	private void initializeValues() {
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			imageView.setImageBitmap(imageProcessor.getBitmap());
		} else {
			restoreSavedValues(data);
		}
	}
	
	private void restoreSavedValues(final Object data) {
		Bundle savedValues = (Bundle)data;
		int bitmapToRead = savedValues.getInt("BITMAP");
		int progress = savedValues.getInt("SLIDER_STATE");
		boolean isRunning = savedValues.getBoolean("IS_RUNNING");
		
		if (bitmapToRead == RESTORE_PREVIEW_BITMAP){
			imageView.setImageBitmap(imageProcessor.getLastResultBitmap());
		} else {
			imageView.setImageBitmap(imageProcessor.getBitmap());
		}
		slider.setProgress(progress);
		if (isRunning){
			onProcessStart();
			imageProcessor.setProcessListener(this);
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Bundle saveObject = new Bundle();
		if (imageProcessor.getLastResultBitmap() == null){
			saveObject.putInt("BITMAP", RESTORE_SAVED_BITMAP);
		} else {
			saveObject.putInt("BITMAP", RESTORE_PREVIEW_BITMAP);
		}
		saveObject.putInt("SLIDER_STATE", slider.getProgress());
		saveObject.putBoolean("IS_RUNNING", isProgressBarVisible());
		return saveObject;
	}
	
	private boolean isProgressBarVisible() {
		return progressBar.getVisibility()==View.VISIBLE?true:false;
	}
	
	private void runImageProcessor() {
		RotateCommand command = new RotateCommand(slider.getProgress());
		currentAngle=slider.getProgress();
		imageProcessor.setProcessListener(this);
		imageProcessor.runCommand(command);
	}

	public void onProcessStart() {
		// turn off buttons and show "processing" animation
		Log.i("Rotate", "Start Processing");
		okButton.setEnabled(false);
		cancelButton.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);
	}

	public void onProcessEnd(Bitmap result) {
		Log.i("Rotate", "End Processing");
		okButton.setEnabled(true);
		cancelButton.setEnabled(true);
		progressBar.setVisibility(View.INVISIBLE);

		imageView.setImageBitmap(result);
		imageView.invalidate();
	}

	private OnClickListener okButtonListener = new OnClickListener() {
		public void onClick(View v) {
			setResult(RESULT_OK);
			imageProcessor.save();
			finish();
		}
	};

	private OnClickListener cancelButtonListener = new OnClickListener() {
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			finish();
		}
	};
	private OnClickListener roteteLeftButtonListener = new OnClickListener() {
		public void onClick(View v) {
			rotate90Image(270);
		}
	};
	private OnClickListener roteteRightButtonListener = new OnClickListener() {
		public void onClick(View v) {
			rotate90Image(90);
		}
	};

	private OnSeekBarChangeListener sliderChangeListener = new OnSeekBarChangeListener() {
		public void onStopTrackingTouch(SeekBar seekBar) {
			// do nothing
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// do nothing
		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (sliderMovedByUser(seekBar, fromUser)) {
				runImageProcessor();
			}
		}

		private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
			return fromUser && seekBar.getId() == slider.getId();
		}
	};
	
	private void rotate90Image(int angle) {
		RotateCommand rc = new RotateCommand();
		currentAngle= (currentAngle+angle)%360;
        rc.setAngle(currentAngle);
        slider.setProgress(currentAngle);
		imageProcessor.setProcessListener(this);
		imageProcessor.runCommand(rc);
	}

}

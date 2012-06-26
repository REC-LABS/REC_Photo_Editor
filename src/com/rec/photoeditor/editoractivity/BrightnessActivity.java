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
import com.rec.photoeditor.graphics.commands.BrightnessCommand;

public class BrightnessActivity extends Activity implements
		ImageProcessorListener {
	private ImageProcessor imageProcessor;
	private ImageView imageView;
	private ImageButton okButton;
	private ImageButton cancelButton;
	private SeekBar slider;
	private View progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brightness_editor);
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
		BrightnessCommand command = new BrightnessCommand(
				sliderValueToBrightness(slider.getProgress()));
		imageProcessor.setProcessListener(this);
		imageProcessor.runCommand(command);
	}

	public void onProcessStart() {
		// turn off buttons and show "processing" animation
		Log.i("Brightness", "Start Processing");
		okButton.setEnabled(false);
		cancelButton.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);
	}

	public void onProcessEnd(Bitmap result) {
		Log.i("Brightness", "End Processing");
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

	private int sliderValueToBrightness(int progress) {
		return progress - 100;
	}

	private OnClickListener cancelButtonListener = new OnClickListener() {
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			imageProcessor.clearProcessListener();
			finish();
		}
	};

	private OnSeekBarChangeListener sliderChangeListener = new OnSeekBarChangeListener() {
		public void onStopTrackingTouch(SeekBar seekBar) {
			// not used here
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// not used here
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

}

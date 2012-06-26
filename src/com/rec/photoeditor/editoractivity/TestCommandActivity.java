package com.rec.photoeditor.editoractivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.rec.photoeditor.R;
import com.rec.photoeditor.graphics.ImageProcessor;
import com.rec.photoeditor.graphics.ImageProcessorListener;
import com.rec.photoeditor.graphics.commands.BlackFrameCommand;
import com.rec.photoeditor.graphics.commands.ImageProcessingCommand;

public class TestCommandActivity extends Activity implements
		ImageProcessorListener {
	private ImageProcessor imageProcessor;
	private ImageView imageView;
	private SeekBar slider;
	private ImageButton okButton;
	private ImageButton cancelButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);
		initializeComponents();
	}

	private void initializeComponents() {
		imageProcessor = ImageProcessor.getInstance();
		imageView = (ImageView) findViewById(R.id.image_view);
		imageView.setImageBitmap(imageProcessor.getBitmap());
		slider = (SeekBar) findViewById(R.id.slider);
		slider.setOnSeekBarChangeListener(sliderChangeListener);
		okButton = (ImageButton) findViewById(R.id.ok_button);
		okButton.setOnClickListener(okButtonListener);
		cancelButton = (ImageButton) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(cancelButtonListener);
	}

	private ImageProcessingCommand crateTestCommand() {
		// return new BlackFrameCommand((float)(100));
		return new BlackFrameCommand();
		// return new GaussianBlurCommand();
		// return new BrightnessCommand(100);
		// return new SharpenCommand(5);
		// return new SepiaCommand();
		// return new TintCommand();
		// return new DecreaseColorDepthCommand(64);
		// return new InvertColorCommand();
		// return new ColorFilterCommand(1.5, 0.5, 0.8);
	}

	private OnSeekBarChangeListener sliderChangeListener = new OnSeekBarChangeListener() {

		public void onStopTrackingTouch(SeekBar seekBar) {
			// do nothing
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// do nothing
		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			runImageProcessor();
		}
	};
	private OnClickListener okButtonListener = new OnClickListener() {

		public void onClick(View v) {
			setResult(RESULT_OK);
			imageProcessor.save();
			finish();
		}
	};

	private void runImageProcessor() {
		ImageProcessingCommand command = crateTestCommand();
		ImageProcessor.getInstance().setProcessListener(this);
		ImageProcessor.getInstance().runCommand(command);
	}

	private OnClickListener cancelButtonListener = new OnClickListener() {
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			imageProcessor.clearProcessListener();
			finish();
		}
	};

	public void onProcessStart() {
		// turn off buttons and show "processing" animation
	}

	public void onProcessEnd(Bitmap result) {
		imageView.setImageBitmap(result);
		imageView.invalidate();
	}

}

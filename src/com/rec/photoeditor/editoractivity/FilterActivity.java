package com.rec.photoeditor.editoractivity;

import static com.rec.photoeditor.editoractivity.EditorSaveConstants.RESTORE_PREVIEW_BITMAP;
import static com.rec.photoeditor.editoractivity.EditorSaveConstants.RESTORE_SAVED_BITMAP;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rec.photoeditor.R;
import com.rec.photoeditor.graphics.CommandsPreset;
import com.rec.photoeditor.graphics.ImageProcessor;
import com.rec.photoeditor.graphics.ImageProcessorListener;
import com.rec.photoeditor.graphics.commands.ImageProcessingCommand;

public class FilterActivity extends Activity implements ImageProcessorListener {
	private ImageView imageView;
	private ImageButton okButton;
	private ImageButton cancelButton;
	private ProgressBar progressBar;
	private ImageProcessor imageProcessor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_activity);
		initializeComponents();
	}

	private void initializeComponents() {
		imageProcessor = ImageProcessor.getInstance();
		imageView = (ImageView) findViewById(R.id.image_view);
		okButton = (ImageButton) findViewById(R.id.ok_button);
		okButton.setOnClickListener(okButtonListener);
		cancelButton = (ImageButton) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(cancelButtonListener);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		gallery = (Gallery) findViewById(R.id.filter_gallery);
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setOnItemClickListener(listener);

		initializeValues();
	}

	private void initializeValues() {
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			imageView.setImageBitmap(imageProcessor.getBitmap());
		} else {
			restoreSavedValues(data);
		}

	}

	private void restoreSavedValues(Object data) {
		Bundle savedValues = (Bundle) data;
		int bitmapToRead = savedValues.getInt("BITMAP");
		boolean isRunning = savedValues.getBoolean("IS_RUNNING");
		int selectedFilterIdx = savedValues.getInt("SELECTED_FILTER_POSITION");

		if (bitmapToRead == RESTORE_PREVIEW_BITMAP) {
			imageView.setImageBitmap(imageProcessor.getLastResultBitmap());
		} else {
			imageView.setImageBitmap(imageProcessor.getBitmap());
		}

		gallery.setSelection(selectedFilterIdx);

		if (isRunning) {
			onProcessStart();
			imageProcessor.setProcessListener(this);
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Bundle saveObject = new Bundle();
		if (imageProcessor.getLastResultBitmap() == null) {
			saveObject.putInt("BITMAP", RESTORE_SAVED_BITMAP);
		} else {
			saveObject.putInt("BITMAP", RESTORE_PREVIEW_BITMAP);
		}
		saveObject.putInt("SELECTED_FILTER_POSITION",
				gallery.getSelectedItemPosition());
		saveObject.putBoolean("IS_RUNNING", isProgressBarVisible());
		return saveObject;
	}

	private boolean isProgressBarVisible() {
		return progressBar.getVisibility() == View.VISIBLE ? true : false;
	}

	private OnItemClickListener listener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {

			runImageProcessor(position);

			Toast.makeText(
					FilterActivity.this,
					"Processing: " + CommandsPreset.Names.get(position),
					Toast.LENGTH_SHORT).show();
		}
	};

	private void runImageProcessor(int position) {
		ImageProcessingCommand command = getCommand(position);
		ImageProcessor.getInstance().setProcessListener(this);
		ImageProcessor.getInstance().runCommand(command);
	}

	private ImageProcessingCommand getCommand(int position) {
		return CommandsPreset.Preset.get(position);
	}

	public class ImageAdapter extends BaseAdapter {
		int galleryItemBackground;
		private Context context;
		private Integer[] mImageIds = CommandsPreset.ImageIds;
			
		public ImageAdapter(Context c) {
			context = c;
			TypedArray attr = context
					.obtainStyledAttributes(R.styleable.FiltersGallery);
			galleryItemBackground = attr
					.getResourceId(
							R.styleable.FiltersGallery_android_galleryItemBackground,
							0);
			attr.recycle();
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(context);

			imageView.setImageResource(mImageIds[position]);
			imageView.setLayoutParams(new Gallery.LayoutParams(150, 150));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setBackgroundResource(galleryItemBackground);

			return imageView;
		}
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
			imageProcessor.clearProcessListener();
			finish();
		}
	};
	private Gallery gallery;

	public void onProcessStart() {
		// turn off buttons and show "processing" animation
		Log.i("FilterActivity", "Start Processing");
		okButton.setEnabled(false);
		cancelButton.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);
	}

	public void onProcessEnd(Bitmap result) {
		Log.i("FilterActivity", "Start Processing");
		okButton.setEnabled(true);
		cancelButton.setEnabled(true);
		progressBar.setVisibility(View.INVISIBLE);

		imageView.setImageBitmap(result);
		imageView.invalidate();
	}

}

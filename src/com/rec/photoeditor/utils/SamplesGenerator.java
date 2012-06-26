package com.rec.photoeditor.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.rec.photoeditor.graphics.CommandsPreset;
import com.rec.photoeditor.graphics.ImageProcessor;
import com.rec.photoeditor.graphics.ImageProcessorListener;
import com.rec.photoeditor.graphics.commands.ImageProcessingCommand;

public class SamplesGenerator implements ImageProcessorListener {
	ImageProcessor imgproc;
	ArrayList<ImageProcessingCommand> cp;
	
	int counter;
	private Context context;
	
	public SamplesGenerator(Context context) {
		imgproc = ImageProcessor.getInstance();		
		cp = CommandsPreset.Preset;
		counter = 0;
		this.context = context;
	}
	
	public void generate(){
		imgproc.setProcessListener(this);	
		runNext();
	}

	private void runNext() {
		ImageProcessingCommand command = cp.get(counter);
		imgproc.runCommand(command);
	}

	public void onProcessStart() {
		// processing startef
		Log.i("Sample Generator", "Processing Started: "+counter);
	}

	public void onProcessEnd(Bitmap result) {
		Log.i("Sample Generator", "Processing END: "+counter);	
		saveBitmap(result);
		counter++;
		if (counter < cp.size()){
			runNext();
		}
	}

	private void saveBitmap(Bitmap bitmap) {
		SaveToStorageUtil.save(bitmap, "Sample_"+counter+".jpg", context);
	}
	
}

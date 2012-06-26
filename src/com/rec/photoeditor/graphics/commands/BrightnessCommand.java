package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

public class BrightnessCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

	private int brightness = 0;

	public BrightnessCommand() {

	}

	public BrightnessCommand(int brightness) {
		setBrightness(brightness);
	}

	public Bitmap process(Bitmap bitmap) {
		Log.i("Image Processing Command", ID+" : "+brightness);
		float b = valueToMatrix(brightness);
		ColorMatrix cm = new ColorMatrix();
		cm.set(new float[] { 1, 0, 0, 0, b, 0, 1, 0, 0, b, 0, 0, 1, 0, b, 0, 0,
				0, 1, 0 });

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColorFilter(new ColorMatrixColorFilter(cm));

		Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());

		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(bitmap, new Matrix(), paint);

		return result;
	}

	private float valueToMatrix(int val) {
		return ((float) val * 2);
	}

	public int getBrightness() {
		return brightness;
	}

	/**
	 * Brightness values between -100 and 100
	 */
	public void setBrightness(int brightness) {
		if (brightness < -100) {
			brightness = -100;
		} else if (brightness > 100) {
			brightness = 100;
		}
		this.brightness = brightness;
	}

	public String getId() {
		return ID;
	}

}

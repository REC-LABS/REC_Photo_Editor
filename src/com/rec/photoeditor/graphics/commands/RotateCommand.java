package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class RotateCommand implements ImageProcessingCommand{
	
	private static final String ID = "com.rec.photoeditor.graphics.commands.RotateCommand";
	
	private static final int FULL_ANGLE = 360;
	private int angle = 0;
	
	public RotateCommand() {
	}
	
	public RotateCommand(int angle) {
		setAngle(angle);
	}

	public Bitmap process(Bitmap bitmap) {	
		Log.i("Image Processing Command", ID+" : "+angle);
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
	
	public int getAngle() {
		return angle;
	}

	/**
	 * Rotation angle in degrees, values between 0 and 360
	 */
	public void setAngle(int angle) {
		angle = angle%FULL_ANGLE;
		if (angle < 0){
			angle+=FULL_ANGLE;
		}
		this.angle = angle;
	}
	
	public String getId() {
		return ID;
	}
}

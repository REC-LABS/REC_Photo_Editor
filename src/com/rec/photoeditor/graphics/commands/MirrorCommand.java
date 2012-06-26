package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class MirrorCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.MirrorCommand";

	public static final int FLIP_VERTICAL = 1;
	public static final int FLIP_HORIZONTAL = 2;
	
	private int type;
	
	public MirrorCommand() {
		this.type = FLIP_HORIZONTAL;
	}
	
	public MirrorCommand(int type) {
		this.type = type;
	}
	
	public Bitmap process(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		if(type == FLIP_VERTICAL) {
			matrix.preScale(1.0f, -1.0f);
		}
		else if(type == FLIP_HORIZONTAL) {
			matrix.preScale(-1.0f, 1.0f);
		} else {
			return null;
		}

		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public String getId() {
		return ID;
	}

}

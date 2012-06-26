package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;

public class EmptyCommand implements ImageProcessingCommand {
	
	public static final String ID = "com.rec.photoeditor.graphics.commands.EmptyCommand";
	
	public Bitmap process(Bitmap bitmap) {
		return bitmap;
	}

	public String getId() {
		return ID;
	}

}

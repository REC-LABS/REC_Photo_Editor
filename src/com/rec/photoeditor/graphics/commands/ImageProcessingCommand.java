package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;

public interface ImageProcessingCommand {
	public Bitmap process(Bitmap bitmap);
	public String getId();
}

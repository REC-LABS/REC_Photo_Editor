package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class CropCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.CropCommand";

	private int x = 0;
	private int y = 0;
	private int width = 1;
	private int height = 1;

	public CropCommand() {
	}

	public CropCommand(int x, int y, int width, int height) {
		if (x < 0) {
			this.x = 0;
			this.width = width + x < 0 ? 1 : width + x;
		} else {
			this.x = x;
			this.width = width;
		}

		if (y < 0) {
			this.y = 0;
			this.height = height + y < 0 ? 1 : height + y;
		} else {
			this.y = y;
			this.height = height;
		}
	}

	public CropCommand(Rect r) {
		this(r.left, r.top, r.width(), r.height());
	}

	public Bitmap process(Bitmap bitmap) {
		Log.i("Image Processing Command", ID+" : "+x+" "+y+" "+width+" "+height);

		if (x + width > bitmap.getWidth()) {
			width = bitmap.getWidth() - x;
		}

		if (y + height > bitmap.getHeight()) {
			height = bitmap.getHeight() - y;
		}
		
		if (width <=0){
			width =1;
		}
		if (height <=0){
			height =1;
		}
		
		return Bitmap.createBitmap(bitmap, x, y, width, height);			
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getId() {
		return ID;
	}

}

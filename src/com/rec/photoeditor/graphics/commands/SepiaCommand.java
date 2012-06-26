package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Color;


public class SepiaCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.SepiaCommand";
	
//	Grayscale constants
//	Some notes about these constants
//	http://gimp-savvy.com/BOOK/index.html?node54.html
	final double RED_FACTOR = 0.299;
	final double GREEN_FACTOR = 0.587;
	final double BLUE_FACTOR = 0.114;
	
	private double red;
	private double green;
	private double blue;
	private int depth;
	
	public SepiaCommand() {
//		default values
		this.red = 2;
		this.green = 1;
		this.blue = 0;
		this.depth = 20; 
	}
	
	public SepiaCommand(double red, double green, double blue, int depth) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.depth = depth;
	}
	
	
	public Bitmap process(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		int A, R, G, B;
		int[] pix = new int[width * height];
	    bitmap.getPixels(pix, 0, width, 0, 0, width, height);
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int index = y * width + x;
				A = (pix[index] >> 24) & 0xff;
				R = ( pix[index] >> 16 ) & 0xff;
		    	G = ( pix[index] >> 8 ) & 0xff;
		    	B = pix[index] & 0xff;
		    	
				B = G = R = (int)(RED_FACTOR * R + GREEN_FACTOR * G + BLUE_FACTOR * B);

				R += (depth * red);
				if(R > 255) { R = 255; }

				G += (depth * green);
				if(G > 255) { G = 255; }

				B += (depth * blue);
				if(B > 255) { B = 255; }

				pix[index] = A<<24 | (R << 16) | (G << 8 ) | B;
			}
		}

		Bitmap result = Bitmap.createBitmap(width, height, bitmap.getConfig());
		result.setPixels(pix, 0, width, 0, 0, width, height);
		pix = null;
		return result;
	}

	
	// getters and setters
	public String getId() {
		return ID;
	}

	public double getRed() {
		return red;
	}

	public void setRed(double red) {
		this.red = red;
	}

	public double getGreen() {
		return green;
	}

	public void setGreen(double green) {
		this.green = green;
	}

	public double getBlue() {
		return blue;
	}

	public void setBlue(double blue) {
		this.blue = blue;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	
}

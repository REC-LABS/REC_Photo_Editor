package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Color;

//About:
//http://en.wikipedia.org/wiki/Gamma_correction
public class GammaCorrectionCommand implements ImageProcessingCommand {
	private static final String ID = "com.rec.photoeditor.graphics.commands.GammaCorrectionCommand";
	
	private double red;
	private double green;
	private double blue;
	
	final int    MAX_SIZE = 256;
	final double MAX_VALUE_DBL = 255.0;
	final int    MAX_VALUE_INT = 255;
	final double REVERSE = 1.0;

	public GammaCorrectionCommand() {
		// set default values
		red= 0.6;
		green= 0.6;
		blue= 0.6;
	}
	
	public GammaCorrectionCommand(double red, double green, double blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Bitmap process(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int A, R, G, B;
		int[] pix = new int[width * height];
	    bitmap.getPixels(pix, 0, width, 0, 0, width, height);

		int[] gammaR = new int[MAX_SIZE];
		int[] gammaG = new int[MAX_SIZE];
		int[] gammaB = new int[MAX_SIZE];

		for(int i = 0; i < MAX_SIZE; ++i) {
			gammaR[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
			gammaG[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
			gammaB[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
		}

		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int index = y * width + x;
			
				A = (pix[index] >> 24) & 0xff;
				R = ( pix[index] >> 16 ) & 0xff;
		    	G = ( pix[index] >> 8 ) & 0xff;
		    	B = pix[index] & 0xff;

				R = gammaR[R];
				G = gammaG[G];
				B = gammaB[B];

				pix[index] = A<<24 | (R << 16) | (G << 8 ) | B;
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, bitmap.getConfig());
		result.setPixels(pix, 0, width, 0, 0, width, height);
		pix = null;
		return result;
	}

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
}
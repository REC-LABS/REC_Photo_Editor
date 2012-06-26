package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;

public class TintCommand implements ImageProcessingCommand {
	
	private static final String ID = "com.rec.photoeditor.graphics.commands.TintCommand";
	
	public static final double PI = 3.14159d;
	public static final double FULL_CIRCLE_DEGREE = 360d;
	public static final double HALF_CIRCLE_DEGREE = 180d;
	public static final double RANGE = 256d;
	
	private int degree;
	
	public TintCommand() {
		this.degree = 30;
	}
	
	public TintCommand(int degree) {
		this.degree = degree;
	}
	
	public Bitmap process(Bitmap src) {

	    int width = src.getWidth();
		int height = src.getHeight();

	    int[] pix = new int[width * height];
	    src.getPixels(pix, 0, width, 0, 0, width, height);

	    int RY, BY, RYY, GYY, BYY, R, G, B, Y;
//	    int GY; // not used
	    double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;
	   
	    int S = (int)(RANGE * Math.sin(angle));
	    int C = (int)(RANGE * Math.cos(angle));

	    for (int y = 0; y < height; y++)
	        for (int x = 0; x < width; x++) {
		    	int index = y * width + x;
		    	int r = ( pix[index] >> 16 ) & 0xff;
		    	int g = ( pix[index] >> 8 ) & 0xff;
		    	int b = pix[index] & 0xff;
		    	RY = ( 70 * r - 59 * g - 11 * b ) / 100;
//		    	GY = (-30 * r + 41 * g - 11 * b ) / 100;
		    	BY = (-30 * r - 59 * g + 89 * b ) / 100;
		    	Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
		    	RYY = ( S * BY + C * RY ) / 256;
		    	BYY = ( C * BY - S * RY ) / 256;
		    	GYY = (-51 * RYY - 19 * BYY ) / 100;
		    	R = Y + RYY;
		    	R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
		    	G = Y + GYY;
		    	G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
		    	B = Y + BYY;
		    	B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
		    	pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
	    	}
	    
	    Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());	   
	    outBitmap.setPixels(pix, 0, width, 0, 0, width, height);
	   
	    pix = null;
	   
	    return outBitmap;
	}

	public String getId() {
		return ID;
	}
	
	public int getDegree() {
		return degree;
	}
	
	public void setDegree(int degree) {
		this.degree = degree;
	}
}

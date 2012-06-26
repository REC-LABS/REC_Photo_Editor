package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ColorFilterCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.ColorFilter.Command";
	private double redFilter;
	private double greenFilter;
	private double blueFilter;

	public ColorFilterCommand() {
		redFilter = 1;
		greenFilter = 1;
		blueFilter = 1;
	}

	/**
	 * Contructor of color filter command
	 * 
	 * @param red
	 *            value of red channel filter
	 * @param green
	 *            value of green channel filter
	 * @param blue
	 *            value of blue channel filter
	 */
	public ColorFilterCommand(double red, double green, double blue) {
		redFilter = red;
		greenFilter = green;
		blueFilter = blue;
	}

	public Bitmap process(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int A, R, G, B;

		int[] pix = new int[width * height];
	    bitmap.getPixels(pix, 0, width, 0, 0, width, height);
		
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				int index = y * width + x;

				A = (pix[index] >> 24) & 0xff;
				R = ( pix[index] >> 16 ) & 0xff;
		    	G = ( pix[index] >> 8 ) & 0xff;
		    	B = pix[index] & 0xff;

				R = (int) (R * redFilter);
				G = (int) (G * greenFilter);
				B = (int) (B * blueFilter);

				R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
		    	G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
		    	B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
				
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

}

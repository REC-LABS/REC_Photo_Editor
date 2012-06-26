package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Color;

public class GrayscaleCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.GrayscaleCommand";
	
/*	Grayscale constants
*	Some notes about these constants
*	http://gimp-savvy.com/BOOK/index.html?node54.html
*/
	final double RED_FACTOR = 0.299;
	final double GREEN_FACTOR = 0.587;
	final double BLUE_FACTOR = 0.114;
	
	public Bitmap process(Bitmap bitmap) {
		int A, R, G, B;
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int[] pix = new int[width * height];
	    bitmap.getPixels(pix, 0, width, 0, 0, width, height);
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				int index = y * width + x;

				A = (pix[index] >> 24) & 0xff;
				R = ( pix[index] >> 16 ) & 0xff;
		    	G = ( pix[index] >> 8 ) & 0xff;
		    	B = pix[index] & 0xff;

				R = G = B = (int)(RED_FACTOR * R + GREEN_FACTOR * G + BLUE_FACTOR * B);

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

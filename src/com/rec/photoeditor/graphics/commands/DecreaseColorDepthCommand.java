package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Color;

public class DecreaseColorDepthCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.DecreaseColorDepthCommand";

	private int bitOffset;
	
	/**
	 * Default constructor with 128 bit offset 
	 */
	public DecreaseColorDepthCommand() {
		this.bitOffset = 128;
	}
	
	public DecreaseColorDepthCommand(int bitOffset) {
		this.bitOffset = bitOffset;
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

				R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
				G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
				B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
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

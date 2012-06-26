package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Color;

public class InvertColorCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.InvertColorCommand";

	public InvertColorCommand() {
	}
	
	public Bitmap process(Bitmap bitmap) {
		int A, R, G, B;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		int[] pix = new int[width * height];
	    bitmap.getPixels(pix, 0, width, 0, 0, width, height);
	    for (int y = 0; y < height; y++)
	    {
	        for (int x = 0; x < width; x++)
	        {
	        	int index = y * width + x;
	        	A = (pix[index] >> 24) & 0xff;
				R = ( pix[index] >> 16 ) & 0xff;
		    	G = ( pix[index] >> 8 ) & 0xff;
		    	B = pix[index] & 0xff;
	            
	            R = 255 - R;
	            G = 255 - G;
	            B = 255 - B;
	            
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

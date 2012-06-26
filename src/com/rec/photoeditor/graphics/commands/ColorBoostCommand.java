package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ColorBoostCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.ColorBoostCommand";

	private int color;
	private int percent;

	public ColorBoostCommand(int color, int percent) {
		this.color = color;
		this.percent = percent;
	}

	public Bitmap process(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pix = new int[width * height];
		bitmap.getPixels(pix, 0, width, 0, 0, width, height);

		int A, R, G, B;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				int index = y * width + x;

				A = (pix[index] >> 24) & 0xff;
				R = (pix[index] >> 16) & 0xff;
				G = (pix[index] >> 8) & 0xff;
				B = pix[index] & 0xff;

				if (color == Color.RED) {
					R = (int) (R * (1 + percent));
					R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
				} else if (color == Color.GREEN) {
					G = (int) (G * (1 + percent));
					G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
				} else if (color == Color.BLUE) {
					B = (int) (B * (1 + percent));
					B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
				}
				pix[index] = A << 24 | (R << 16) | (G << 8) | B;
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

	public int getPercet() {
		return percent;
	}

	public void setPercent(int percentOfBoost) {
		this.percent = percentOfBoost;
	}

	public int getPrimitiveColor() {
		return color;
	}

	/**
	 * set primitive color to boost. Accept only {@code Color.RED},
	 * {@code Color.GREEN} and {@code Color.BLUE}
	 * 
	 * @param color
	 *            primitive color to boost
	 */
	public void setPrimitiveColor(int color) {
		if (color == Color.RED || color == Color.GREEN || color == Color.BLUE) {
			this.color = color;
		} else {
			this.color = 0;
		}
	}

}

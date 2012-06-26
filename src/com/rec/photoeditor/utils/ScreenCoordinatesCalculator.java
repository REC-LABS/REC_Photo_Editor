package com.rec.photoeditor.utils;

import android.graphics.Matrix;

public class ScreenCoordinatesCalculator {
	public static int[] screenToImage(int[] location, Matrix imageMatrix) {
		int[] result = new int[2];
		float[] values = getImageMatrixValues(imageMatrix);

		result[0] = (int) ((location[0] - values[2]) / values[0]);
		result[1] = (int) ((location[1] - values[5]) / values[4]);
		return result;
	}

	public static int[] imageToScreen(int[] location, Matrix imageMatrix) {
		int[] result = new int[2];
		float[] values = getImageMatrixValues(imageMatrix);

		result[0] = (int) (location[0] * values[0] + values[2]);
		result[1] = (int) (location[1] * values[4] + values[5]);
		return result;
	}

	private static float[] getImageMatrixValues(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		return values;
	}
}

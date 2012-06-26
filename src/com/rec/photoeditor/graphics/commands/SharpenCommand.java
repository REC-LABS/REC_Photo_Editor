package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;

import com.rec.photoeditor.graphics.ConvolutionMatrix;

public class SharpenCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.SharpenCommand";

	public int weight = 0;

	private double[][] SharpConfig;

	public SharpenCommand(int weight) {
		this.weight = weight;
		SharpConfig = new double[][] { { 0, -2, 0 }, { -2, weight, -2 },
				{ 0, -2, 0 } };
	}

	public Bitmap process(Bitmap bitmap) {
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(SharpConfig);
		convMatrix.Factor = weight - 8;
		convMatrix.Offset = 0;
		return ConvolutionMatrix.computeConvolution(bitmap, convMatrix);
	}

	public String getId() {
		return ID;
	}
}

package com.rec.photoeditor.graphics.commands;

import com.rec.photoeditor.graphics.ConvolutionMatrix;

import android.graphics.Bitmap;

public class EmbossCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.EmbossCommand";

	private double[][] config = new double[][] {
			{ -2, -1, 0 },
			{ -1, 1, 1 }, 
			{ 0, 1, 2 } };

	public EmbossCommand() {
	}

	public Bitmap process(Bitmap bitmap) {
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(config);
		convMatrix.Factor = 1;
		convMatrix.Offset = 0;
		return ConvolutionMatrix.computeConvolution(bitmap, convMatrix);
	}

	public String getId() {
		return ID;
	}

}

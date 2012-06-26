package com.rec.photoeditor.graphics;

import java.util.ArrayList;

import android.graphics.Color;

import com.rec.photoeditor.R;
import com.rec.photoeditor.graphics.commands.BlackFrameCommand;
import com.rec.photoeditor.graphics.commands.ColorBoostCommand;
import com.rec.photoeditor.graphics.commands.ColorFilterCommand;
import com.rec.photoeditor.graphics.commands.DecreaseColorDepthCommand;
import com.rec.photoeditor.graphics.commands.EmbossCommand;
import com.rec.photoeditor.graphics.commands.EmptyCommand;
import com.rec.photoeditor.graphics.commands.GammaCorrectionCommand;
import com.rec.photoeditor.graphics.commands.GaussianBlurCommand;
import com.rec.photoeditor.graphics.commands.GrayscaleCommand;
import com.rec.photoeditor.graphics.commands.ImageProcessingCommand;
import com.rec.photoeditor.graphics.commands.InvertColorCommand;
import com.rec.photoeditor.graphics.commands.MirrorCommand;
import com.rec.photoeditor.graphics.commands.SepiaCommand;
import com.rec.photoeditor.graphics.commands.SharpenCommand;
import com.rec.photoeditor.graphics.commands.TintCommand;

public class CommandsPreset {

	public static final ArrayList<ImageProcessingCommand> Preset = new ArrayList<ImageProcessingCommand>();
	public static final ArrayList<String> Names = new ArrayList<String>();

	static {
		Preset.add(new EmptyCommand());
		Names.add("No Filter");
		Preset.add(new GaussianBlurCommand());
		Names.add("Gaussian Blur");
		Preset.add(new GrayscaleCommand());
		Names.add("Grayscale");
		Preset.add(new TintCommand(30));
		Names.add("Tint 1");
		Preset.add(new TintCommand(70));
		Names.add("Tint 2");
		Preset.add(new BlackFrameCommand());
		Names.add("Black Frame");
		Preset.add(new ColorBoostCommand(Color.RED, 20));
		Names.add("Red Boost");
		Preset.add(new ColorBoostCommand(Color.GREEN, 20));
		Names.add("Green Boost");
		Preset.add(new ColorBoostCommand(Color.BLUE, 20));
		Names.add("Blue Boost");
		Preset.add(new ColorFilterCommand(1.1, 0.7, 0.7));
		Names.add("Color Filter 1");
		Preset.add(new ColorFilterCommand(0.7, 1.1, 0.7));
		Names.add("Color Filter 2");
		Preset.add(new ColorFilterCommand(0.7, 0.7, 1.1));
		Names.add("Color Filter 3");
		Preset.add(new ColorFilterCommand(1.3, 1.1, 0.8));
		Names.add("Color Filter 4");
		Preset.add(new DecreaseColorDepthCommand(128));
		Names.add("Decrease Color Depth");
		Preset.add(new GammaCorrectionCommand(0.6, 0.5, 0.7));
		Names.add("Gamma Correction");
		Preset.add(new InvertColorCommand());
		Names.add("Invert Color");
		Preset.add(new MirrorCommand());
		Names.add("Mirror");
		Preset.add(new SepiaCommand(2, 1, 0, 20));
		Names.add("Sepia");
		Preset.add(new SepiaCommand(2, 2, 0, 20));
		Names.add("Sepia 2");
		Preset.add(new SepiaCommand(1.62, 0.78, 1.21, 20));
		Names.add("Sepia 3");
		Preset.add(new SepiaCommand(1.62, 1.28, 1.01, 45));
		Names.add("Sepia 4");
		Preset.add(new SharpenCommand(13));
		Names.add("Sharpen");
		Preset.add(new EmbossCommand());
		Names.add("Emboss");
	}

	public static final Integer[] ImageIds = new Integer[] {
			R.drawable.sample_00, R.drawable.sample_01, R.drawable.sample_02,
			R.drawable.sample_03, R.drawable.sample_04, R.drawable.sample_05,
			R.drawable.sample_06, R.drawable.sample_07, R.drawable.sample_08,
			R.drawable.sample_09, R.drawable.sample_10, R.drawable.sample_11,
			R.drawable.sample_12, R.drawable.sample_13, R.drawable.sample_14,
			R.drawable.sample_15, R.drawable.sample_16, R.drawable.sample_17,
			R.drawable.sample_18, R.drawable.sample_19, R.drawable.sample_20,
			R.drawable.sample_21, R.drawable.sample_22 };
}

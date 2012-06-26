package com.rec.photoeditor.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BlackFrameCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.BlackFrameCommand";
	
	private float round = -1;
	private float border = -1;

	/**
	 * Constructor with custom rounded corners
	 * 
	 * @param round
	 *            round
	 * @param border
	 *            boder width
	 */
	public BlackFrameCommand(float round, float border) {
		this.setRound(round);
		this.setBorder(border);
	}

	/**
	 * Constructor for frame with default size
	 */
	public BlackFrameCommand() {

	}

	public Bitmap process(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int r = (int) (round > 0 ? round : getProportionalRound(width, height));
		int b = (int) (border > 0 ? border : getProportionalBorder(width,
				height));
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);

		final Rect rect = new Rect(b, b, width - b, height - b);
		final RectF rectF = new RectF(rect);

		canvas.drawRoundRect(rectF, r, r, paint);

		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return result;
	}

	private float getProportionalRound(int width, int height) {
		int min = Math.min(width, height);

		return (float) (min / 10.0);
	}

	private float getProportionalBorder(int width, int height) {
		int min = Math.min(width, height);
		return (float) (min / 50.0);
	}

	public float getRound() {
		return round;
	}

	public void setRound(float round) {
		this.round = round;
	}

	public float getBorder() {
		return border;
	}

	public void setBorder(float border) {
		this.border = border;
	}

	public String getId() {
		return ID;
	}
}

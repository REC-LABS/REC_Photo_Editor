package com.rec.photoeditor.editoractivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.rec.photoeditor.R;
import com.rec.photoeditor.utils.ScreenCoordinatesCalculator;

public class CropImageView extends ImageView {
	// Constants
	private static final int UPPER_LEFT = 1;
	private static final int UPPER_RIGHT = 2;
	private static final int LOWER_LEFT = 3;
	private static final int LOWER_RIGHT = 4;
	private static final int CENTER = 5;
	private static final int MAX_DISTANCE_FROM_CORNER_TO_DRAG = 75;
	private static final int MIN_WIDTH_PX = 40;
	private static final int MIN_HEIGHT_PX = 40;

	// Region of Interest variables
	private int roiX = 100;
	private int roiY = 100;
	private int roiWidth = 100;
	private int roiHeight = 100;
	private int selectedCorner;
	private boolean isDragging;
	private Bitmap cornerMarker;

	// constructors
	public CropImageView(Context context) {
		super(context);
		init();
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		cornerMarker = BitmapFactory.decodeResource(getResources(),
				R.drawable.conrer_48);
	}

	public Rect getRegionOfInterest() {
		int lu[] = ScreenCoordinatesCalculator.screenToImage(new int[] { roiX,
				roiY }, getImageMatrix());
		int rb[] = ScreenCoordinatesCalculator.screenToImage(new int[] {
				roiX + roiWidth, roiY + roiHeight }, getImageMatrix());
		return new Rect(lu[0], lu[1], rb[0], rb[1]);
	}

	Rect notDrawedRoi;

	public void setRegionOfInterest(Rect rect) {
		notDrawedRoi = rect;
		saveRoi(rect);
		this.invalidate();
	}

	private void saveRoi(Rect rect) {
		int lu[] = ScreenCoordinatesCalculator.imageToScreen(new int[] {
				rect.left, rect.top }, getImageMatrix());
		int rb[] = ScreenCoordinatesCalculator.imageToScreen(new int[] {
				rect.right, rect.bottom }, getImageMatrix());
		roiX = lu[0];
		roiY = lu[1];
		roiWidth = rb[0] - roiX;
		roiHeight = rb[1] - roiY;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// looks weird, but it's neccessary: saveRoi must be after onDraw,
		// otherwise getImageMatrix did not return proper values
		if (notDrawedRoi != null) {
			saveRoi(notDrawedRoi);
			notDrawedRoi = null;
		}
		drawRoiRectangle(canvas);
		drawAllMarkers(canvas);
	}

	private void drawRoiRectangle(Canvas canvas) {
		canvas.save();
		canvas.clipRect(
				new Rect(roiX, roiY, roiX + roiWidth, roiY + roiHeight),
				Region.Op.DIFFERENCE);
		canvas.drawARGB(140, 0, 0, 0);
		canvas.restore();
	}

	private void drawAllMarkers(Canvas canvas) {
		Rect src = new Rect(0, 0, cornerMarker.getWidth(),
				cornerMarker.getHeight());
		drawCornerMarker(canvas, src, roiX, roiY);
		drawCornerMarker(canvas, src, roiX, roiY + roiHeight);
		drawCornerMarker(canvas, src, roiX + roiWidth, roiY);
		drawCornerMarker(canvas, src, roiX + roiWidth, roiY + roiHeight);
		drawCornerMarker(canvas, src, roiX + roiWidth/2, roiY + roiHeight/2);
	}

	private void drawCornerMarker(Canvas canvas, Rect src, int x, int y) {
		Rect dst = new Rect(x - cornerMarker.getWidth() / 2, y
				- cornerMarker.getHeight() / 2,
				x + cornerMarker.getWidth() / 2, y + cornerMarker.getHeight()
						/ 2);
		canvas.drawBitmap(cornerMarker, src, dst, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isDragging = true;
			selectNearestCorner((int) event.getX(), (int) event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {

			moveSelectedCorner((int) event.getX(), (int) event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (isDragging) {
				isDragging = false;
				moveSelectedCorner((int) event.getX(), (int) event.getY());
			}
		}

		return true;
	}

	private void moveSelectedCorner(int rawX, int rawY) {
		switch (selectedCorner) {
		case UPPER_LEFT:
			moveLeftBound(rawX);
			moveUpperBound(rawY);
			break;
		case LOWER_LEFT:
			moveLeftBound(rawX);
			moveLowerBound(rawY);
			break;
		case UPPER_RIGHT:
			moveRightBound(rawX);
			moveUpperBound(rawY);
			break;
		case LOWER_RIGHT:
			moveRightBound(rawX);
			moveLowerBound(rawY);
			break;
		case CENTER:
			moveSection(rawX, rawY);
			break;
		default:
			break;
		}

		invalidate();
	}

	private void moveLowerBound(int rawY) {
		if (rawY - roiY < MIN_HEIGHT_PX) {
			roiHeight = MIN_HEIGHT_PX;
		} else {
			roiHeight = rawY - roiY;
		}
	}

	private void moveUpperBound(int rawY) {
		if (roiHeight - rawY + roiY < MIN_HEIGHT_PX) {
			roiY = roiY + roiHeight - MIN_HEIGHT_PX;
			roiHeight = MIN_HEIGHT_PX;
		} else {
			roiHeight -= rawY - roiY;
			roiY = rawY;
		}
	}

	private void moveRightBound(int rawX) {
		if (rawX - roiX < MIN_WIDTH_PX) {
			roiWidth = MIN_WIDTH_PX;
		} else {
			roiWidth = rawX - roiX;
		}
	}

	private void moveLeftBound(int rawX) {
		if (roiWidth - rawX + roiX < MIN_WIDTH_PX) {
			roiX = roiX + roiWidth - MIN_WIDTH_PX;
			roiWidth = MIN_WIDTH_PX;
		} else {
			roiWidth -= rawX - roiX;
			roiX = rawX;
		}
	}
	private void moveSection(int rawX, int rawY) {
		
		roiX +=  (rawX - (roiX + roiX / 2) );
		roiY +=  (rawY  - (roiY + roiY / 2) );
		
		 
		
	}

	private void selectNearestCorner(int rawX, int rawY) {
		int minDistance = MAX_DISTANCE_FROM_CORNER_TO_DRAG;
		selectedCorner = 0;

		minDistance = cornerDistanceAndSelection(rawX, rawY, roiX, roiY,
				minDistance, UPPER_LEFT);
		minDistance = cornerDistanceAndSelection(rawX, rawY, roiX, roiY
				+ roiHeight, minDistance, LOWER_LEFT);
		minDistance = cornerDistanceAndSelection(rawX, rawY, roiX + roiWidth,
				roiY, minDistance, UPPER_RIGHT);
		minDistance = cornerDistanceAndSelection(rawX, rawY, roiX + roiWidth,
				roiY + roiHeight, minDistance, LOWER_RIGHT);
		minDistance = cornerDistanceAndSelection(rawX, rawY, roiX + roiWidth / 2,
				roiY + roiHeight / 2, minDistance, CENTER);
		
		
		
	}

	private int cornerDistanceAndSelection(int tapX, int rawY, int cornerX,
			int cornerY, int minDistance, int corner) {
		int distance;
		distance = calculateDistance(tapX, rawY, cornerX, cornerY);
		if (minDistance > distance) {
			minDistance = distance;
			selectedCorner = corner;
		}
		return minDistance;
	}

	/**
	 * Euclidean distance between two points (x1, y1) and (x2, y2)
	 */
	private int calculateDistance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
}

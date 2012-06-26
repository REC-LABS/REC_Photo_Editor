package com.rec.photoeditor.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class BitmapScalingUtil {
	private static final int MAX_IMAGE_DIMENSION = 972;

	public static byte[] bitmapFromUriToByteArray(Context context, Uri photoUri)
			throws IOException {
		Bitmap srcBitmap = bitmapFromUri(context, photoUri);
		String type = context.getContentResolver().getType(photoUri);
		byte[] bMapArray = bitmapToByteArray(srcBitmap, type);
		return bMapArray;
	}

	public static Bitmap bitmapFromUri(Context context, Uri photoUri)
			throws FileNotFoundException, IOException {
		InputStream is = context.getContentResolver().openInputStream(photoUri);
		BitmapFactory.Options dbo = new BitmapFactory.Options();
		dbo.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, dbo);
		is.close();

		int rotatedWidth, rotatedHeight;
		
		int orientation = 0;
		
		if (photoUri.toString().contains("content:/")){ 
			orientation = getOrientation(context, photoUri);
			Log.i("Photo Editor", "Orientation: " + orientation);
		} else {
			int orientationFormExif = getOrientationFromExif(photoUri, context);
			orientation = decodeExifOrientation(orientationFormExif);
			Log.i("Photo Editor", "Orientation form Exif: " + orientation);
		}
	
		if (orientation == 90 || orientation == 270) {
			rotatedWidth = dbo.outHeight;
			rotatedHeight = dbo.outWidth;
		} else {
			rotatedWidth = dbo.outWidth;
			rotatedHeight = dbo.outHeight;
		}

		Bitmap srcBitmap = readScaledBitmapFromUri(photoUri, context,
				rotatedWidth, rotatedHeight);

		srcBitmap = setProperOrientation(orientation, srcBitmap);
		return srcBitmap;
	}

	public static byte[] bitmapToByteArray(Bitmap srcBitmap, String type)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (type.equals("image/png")) {
			srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		} else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
			srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		}
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return bMapArray;
	}

	/*
	 * if the orientation is not 0 (or -1, which means we don't know), we have
	 * to do a rotation.
	 */
	private static Bitmap setProperOrientation(int orientation, Bitmap srcBitmap) {
		if (orientation > 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(orientation);

			srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
					srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		}
		return srcBitmap;
	}

	private static Bitmap readScaledBitmapFromUri(Uri photoUri,
			Context context, int width, int height)
			throws FileNotFoundException, IOException {
		Log.i("Photo Editor", "Read Scaled Bitmap: " + width + " " + height);
		InputStream is;
		Bitmap srcBitmap;
		is = context.getContentResolver().openInputStream(photoUri);
		if (width > MAX_IMAGE_DIMENSION || height > MAX_IMAGE_DIMENSION) {
			float ratio = calculateScaleRatio(width, height);
			Log.i("Photo Editor", "Scaled Bitmap: " + ratio);
			srcBitmap = readRoughScaledBitmap(is, ratio);
			ratio = calculateScaleRatio(srcBitmap.getWidth(),
					srcBitmap.getHeight());
			srcBitmap = scaleBitmap(srcBitmap, ratio);
		} else {
			Log.i("Photo Editor", "NOT Scaled Bitmap ");
			srcBitmap = BitmapFactory.decodeStream(is);
		}
		is.close();
		return srcBitmap;
	}

	private static float calculateScaleRatio(int width, int height) {
		float widthRatio = ((float) width) / ((float) MAX_IMAGE_DIMENSION);
		float heightRatio = ((float) height) / ((float) MAX_IMAGE_DIMENSION);
		float maxRatio = Math.max(widthRatio, heightRatio);
		return maxRatio;
	}

	private static Bitmap readRoughScaledBitmap(InputStream is, float maxRatio) {
		Bitmap result;
		// Create the bitmap from file
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = (int) maxRatio;
		result = BitmapFactory.decodeStream(is, null, options);
		if (result != null) {
			Log.i("REC Photo Editor", "Read Scaled Bitmap Result wtf: "
					+ result.getWidth() + " " + result.getHeight());
			Log.i("REC Photo Editor", "MaxRatio wtf: " + maxRatio);
		}
		return result;
	}

	private static Bitmap scaleBitmap(Bitmap bitmap, float ratio) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(1f / ratio, 1f / ratio);

		Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return result;
	}

	private static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	private static int getOrientationFromExif(Uri imageUri, Context context) {
		int orientation = -1;
		Log.i("Photo Editor", "imageUri = " + imageUri);
		// File imageFile = new File(getRealPathFromUri(imageUri, context));
		File imageFile = new File(imageUri.getPath());
		try {
			ExifInterface exif;
			Log.i("Photo Editor",
					"imageFile.getAbsolutePath() = "
							+ imageFile.getAbsolutePath());
			exif = new ExifInterface(imageFile.getAbsolutePath());
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return orientation;
	}

	private static String getRealPathFromUri(Uri contentUri, Context context) {
		Cursor cursor = context.getContentResolver().query(contentUri, null,
				null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	private static int decodeExifOrientation(int orientation) {
		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			orientation = 0;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			orientation = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			orientation = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			orientation = 270;
			break;
		default:
			break;
		}
		return orientation;
	}

}

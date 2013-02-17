package jp.paming;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class PictureUtil {
	private final int TARGET_WIDTH = 300;
	private final int TARGET_HEIGHT = 300;
	
	public static Bitmap resize(Bitmap picture, int targetWidth, int targetHeight) {
	    if (picture == null || targetHeight < 0 || targetWidth < 0) {
	        return null;
	    }

	    int pictureWidth = picture.getWidth();
	    int pictureHeight = picture.getHeight();
	    float scale = Math.min((float) targetWidth / pictureWidth, (float) targetHeight / pictureHeight); // (1)

	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);
	    
	    return Bitmap.createBitmap(picture, 0, 0, pictureWidth, pictureHeight, matrix, true);
	}
}

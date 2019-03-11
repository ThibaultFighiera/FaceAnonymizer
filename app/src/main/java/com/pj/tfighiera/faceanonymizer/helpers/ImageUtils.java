package com.pj.tfighiera.faceanonymizer.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Utilities methods to manage images
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version 1.0
 */
public class ImageUtils
{
	@Nullable
	public static Bitmap openBitmap(@NonNull Context context, @Nullable Uri uri)
	{
		if (uri != null)
		{
			try
			{
				InputStream imageStream = context.getContentResolver()
				                                 .openInputStream(uri);
				return BitmapFactory.decodeStream(imageStream);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	@NonNull
	public static Bitmap rotate(@NonNull Bitmap bitmap, float angle)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
}

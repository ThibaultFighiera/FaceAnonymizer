package com.pj.tfighiera.faceanonymizer.helpers;

import com.pj.tfighiera.faceanonymizer.model.ImageModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * //TODO : Add a class header comments
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version //TODO : add version
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
				InputStream imageStream = context.getContentResolver().openInputStream(uri);
				return BitmapFactory.decodeStream(imageStream);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}

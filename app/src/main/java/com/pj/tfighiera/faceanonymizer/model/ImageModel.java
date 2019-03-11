package com.pj.tfighiera.faceanonymizer.model;

import com.pj.tfighiera.faceanonymizer.helpers.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Model containing information about an image
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version 1.0
 */
public class ImageModel
{
	private static final String SAVED_PHOTO = "saved-photo";
	private static final String SAVED_FACE_COUNT = "saved-face-count";
	@Nullable
	private Bitmap mBitmap;
	@Nullable
	private Uri mImageUri;
	private int mCount;

	public ImageModel(@NonNull Context context, @Nullable Uri imageUri)
	{
		if (imageUri != null)
		{
			mImageUri = imageUri;
			mBitmap = ImageUtils.openBitmap(context, imageUri);
		}
	}

	@Nullable
	public Bitmap getBitmap()
	{
		return mBitmap;
	}

	public void setBitmap(@Nullable Bitmap bitmap)
	{
		mBitmap = bitmap;
	}

	public void save(@NonNull Bundle outState)
	{
		outState.putParcelable(SAVED_PHOTO, mImageUri);
		outState.putInt(SAVED_FACE_COUNT, mCount);
	}

	@Nullable
	public static ImageModel restore(Context context, Bundle savedInstanceState)
	{
		ImageModel imageModel = null;
		Uri imageUri = savedInstanceState.getParcelable(SAVED_PHOTO);
		if (imageUri != null)
		{
			imageModel = new ImageModel(context, imageUri);
			imageModel.mCount = savedInstanceState.getInt(SAVED_FACE_COUNT);
		}
		return imageModel;
	}

	public int getCount()
	{
		return mCount;
	}

	public void setCount(int count)
	{
		mCount = count;
	}
}

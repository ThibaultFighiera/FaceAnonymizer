package com.pj.tfighiera.faceanonymizer.model;

import com.pj.tfighiera.faceanonymizer.helpers.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * //TODO : Add a class header comments
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version //TODO : add version
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
		if(imageUri != null)
		{
			mImageUri = imageUri;
			mBitmap = ImageUtils.openBitmap(context, imageUri);
		}
	}

	public int getWidth()
	{
		return mBitmap == null
		       ? 0
		       : mBitmap.getWidth();
	}

	public int getHeight()
	{
		return mBitmap == null
		       ? 0
		       : mBitmap.getHeight();
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

	public void save(Bundle outState)
	{
		outState.putParcelable(SAVED_PHOTO, mImageUri);
		outState.putInt(SAVED_FACE_COUNT, mCount);
	}

	@NonNull
	public static ImageModel restore(Context context, Bundle savedInstanceState)
	{
		Uri imageUri = savedInstanceState.getParcelable(SAVED_PHOTO);
		ImageModel imageModel = new ImageModel(context, imageUri);
		imageModel.mCount = savedInstanceState.getInt(SAVED_FACE_COUNT);
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

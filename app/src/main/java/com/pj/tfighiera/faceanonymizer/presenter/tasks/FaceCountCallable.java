package com.pj.tfighiera.faceanonymizer.presenter.tasks;

import com.pj.tfighiera.faceanonymizer.helpers.FaceDetectorFacade;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Callable;

/**
 * Detection task allowing to count how many faces where found
 *
 * created on 21/07/2017
 *
 * @author tfi
 * @version 1.0
 */
public class FaceCountCallable implements Callable<Integer>
{
	@NonNull
	private final Context mContext;
	@Nullable
	private final Bitmap mBitmap;

	public FaceCountCallable(@NonNull Context context, @Nullable Bitmap bitmap)
	{
		mContext = context;
		mBitmap = bitmap;
	}

	@Override
	public Integer call() throws Exception
	{
		FaceDetectorFacade mFaceDetector = new FaceDetectorFacade(mContext);
		return mBitmap == null
		       ? 0
		       : mFaceDetector.detect(mBitmap)
		                      .size();
	}
}

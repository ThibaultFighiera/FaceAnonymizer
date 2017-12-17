package com.pj.tfighiera.faceanonymizer.presenter.tasks;

import com.pj.tfighiera.faceanonymizer.helpers.FaceDetectorFacade;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Detection task allowing to count how many faces where found
 *
 * created on 21/07/2017
 *
 * @author tfi
 * @version 1.0
 */
public class FaceDetectionTask extends AsyncTask<Bitmap, Void, Integer>
{
	@Nullable
	private final WeakReference<FaceDetectionDelegate> mDelegate;
	@NonNull
	private final FaceDetectorFacade mFaceDetector;

	public FaceDetectionTask(@NonNull Context context, @Nullable FaceDetectionDelegate delegate)
	{
		mDelegate = new WeakReference<>(delegate);
		mFaceDetector = new FaceDetectorFacade(context);
	}

	@Override
	protected Integer doInBackground(Bitmap... bitmaps)
	{
		Bitmap bitmap = bitmaps[0];
		return bitmap == null
		       ? 0
		       : mFaceDetector.detect(bitmap)
		                      .size();
	}

	@Override
	protected void onPostExecute(Integer count)
	{
		if (mDelegate != null && mDelegate.get() != null)
		{
			mDelegate.get()
			         .onFaceDetected(count);
		}
	}

	public interface FaceDetectionDelegate
	{
		void onFaceDetected(int count);
	}
}

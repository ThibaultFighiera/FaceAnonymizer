package com.pj.tfighiera.faceanonymizer.tasks;

import com.google.android.gms.vision.face.Face;

import com.pj.tfighiera.faceanonymizer.helpers.Blurrer;
import com.pj.tfighiera.faceanonymizer.helpers.FaceDetectorHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

/**
 * created on 21/07/2017
 *
 * @author tfi
 */

public class AnonymizerTask extends AsyncTask<Bitmap, Void, Bitmap>
{
	@NonNull
	private final Context mContext;
	@NonNull
	private final FaceDetectorHelper mFaceDetector;
	@Nullable
	private final WeakReference<AnonymizerDelegate> mAnonymizerDelegate;

	public AnonymizerTask(@NonNull Context context, @Nullable AnonymizerDelegate anonymizerDelegate)
	{
		mContext = context;
		mAnonymizerDelegate = new WeakReference<>(anonymizerDelegate);
		mFaceDetector = new FaceDetectorHelper(context);
	}

	@Nullable
	private Bitmap createFaceMask(@NonNull Context context, @Nullable SparseArray<Face> faces, @NonNull Bitmap originalImage)
	{
		Bitmap maskedBitmap = Bitmap.createBitmap(originalImage.getWidth(), originalImage.getHeight(), Bitmap.Config.RGB_565);
		if (faces != null)
		{
			Canvas masksCanvas = new Canvas(maskedBitmap);
			masksCanvas.drawBitmap(originalImage, 0, 0, null);
			for (int i = 0; i < faces.size(); i++)
			{
				Face face = faces.valueAt(i);
				Blurrer blurrer = new Blurrer.BlurrerBuilder(originalImage).setPosition(face.getPosition())
				                                                           .setWidth(face.getWidth())
				                                                           .setHeight(face.getHeight())
				                                                           .build();
				masksCanvas.drawBitmap(blurrer.createBlurMask(context), face.getPosition().x, face.getPosition().y, null);
			}
		}
		return maskedBitmap;
	}

	@Override
	protected Bitmap doInBackground(@NonNull Bitmap... bitmaps)
	{
		if (!mFaceDetector.isOperational())
		{
			return bitmaps[0];
		}
		SparseArray<Face> faces = mFaceDetector.detect(bitmaps[0]);
		return createFaceMask(mContext, faces, bitmaps[0]);
	}

	@Override
	protected void onPostExecute(@NonNull Bitmap mask)
	{
		if (mAnonymizerDelegate != null && mAnonymizerDelegate.get() != null)
		{
			mAnonymizerDelegate.get()
			                   .onAnonymizationSuccess(mask);
		}
	}

	public interface AnonymizerDelegate
	{
		void onAnonymizationSuccess(@NonNull Bitmap mask);
	}
}


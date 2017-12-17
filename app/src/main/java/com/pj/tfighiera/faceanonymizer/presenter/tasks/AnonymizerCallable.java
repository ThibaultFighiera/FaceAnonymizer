package com.pj.tfighiera.faceanonymizer.presenter.tasks;

import com.google.android.gms.vision.face.Face;

import com.pj.tfighiera.faceanonymizer.App;
import com.pj.tfighiera.faceanonymizer.helpers.Blurrer;
import com.pj.tfighiera.faceanonymizer.helpers.FaceDetectorFacade;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.concurrent.Callable;

/**
 * Anonymize Face task on an a given Bitmap
 *
 * created on 21/07/2017
 *
 * @author tfi
 * @version 1.0
 */
public class AnonymizerCallable implements Callable<Bitmap>
{
	@NonNull
	private final FaceDetectorFacade mFaceDetector;
	@NonNull
	private Bitmap mBitmap;

	public AnonymizerCallable(@NonNull Context context, @NonNull Bitmap bitmap)
	{
		mBitmap = bitmap;
		mFaceDetector = new FaceDetectorFacade(context);
	}

	@Override
	public Bitmap call() throws Exception
	{
		if (!mFaceDetector.isOperational())
		{
			return mBitmap;
		}
		SparseArray<Face> faces = mFaceDetector.detect(mBitmap);
		return createFaceMask(App.getContext(), faces, mBitmap);
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
				Blurrer.BlurrerBuilder blurrer = new Blurrer.BlurrerBuilder(originalImage).setPosition(face.getPosition())
				                                                                          .setWidth(face.getWidth())
				                                                                          .setHeight(face.getHeight());
				masksCanvas.drawBitmap(blurrer.build()
				                              .createBlurMask(context), face.getPosition().x, face.getPosition().y, null);
			}
		}
		return maskedBitmap;
	}
}


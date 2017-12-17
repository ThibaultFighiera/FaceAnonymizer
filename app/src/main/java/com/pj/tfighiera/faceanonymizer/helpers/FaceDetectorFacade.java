package com.pj.tfighiera.faceanonymizer.helpers;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.SparseArray;

/**
 * Face detector Facade
 * created on 21/07/2017
 *
 * @author tfi
 */
public class FaceDetectorFacade
{
	@NonNull
	private final FaceDetector mFaceDetector;

	public FaceDetectorFacade(@NonNull Context context)
	{
		mFaceDetector = new FaceDetector.Builder(context).setMode(FaceDetector.ACCURATE_MODE)
		                                                 .setLandmarkType(FaceDetector.ALL_LANDMARKS)
		                                                 .setTrackingEnabled(false)
		                                                 .setMinFaceSize(0f)
		                                                 .build();
	}

	@WorkerThread
	@NonNull
	public SparseArray<Face> detect(@NonNull Bitmap bitmap)
	{
		Frame frame = new Frame.Builder().setBitmap(bitmap)
		                                 .build();
		SparseArray<Face> faces = mFaceDetector.detect(frame);
		mFaceDetector.release();
		return faces;
	}

	public boolean isOperational()
	{
		return mFaceDetector.isOperational();
	}
}

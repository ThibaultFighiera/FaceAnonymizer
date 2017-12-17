package com.pj.tfighiera.faceanonymizer.presenter;

import com.pj.tfighiera.faceanonymizer.R;
import com.pj.tfighiera.faceanonymizer.helpers.ImageUtils;
import com.pj.tfighiera.faceanonymizer.model.ImageModel;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.AnonymizerTask;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.FaceDetectionTask;
import com.pj.tfighiera.faceanonymizer.view.MainViewHolder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Manage View Model and anonymizing tasks
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version 1.0
 */
public class MainPresenter implements FaceDetectionTask.FaceDetectionDelegate, AnonymizerTask.AnonymizerDelegate
{
	@Nullable
	private ProgressDialog mDialog;
	@NonNull
	private final MainViewHolder mMainViewHolder;
	@NonNull
	private Delegate mDelegate;
	@Nullable
	private ImageModel mImageModel;
	@Nullable
	private AsyncTask mCurrentTask;
	@NonNull
	private final Context mContext;

	public MainPresenter(@NonNull Activity activity, @NonNull Delegate delegate)
	{
		ButterKnife.bind(this, activity);
		mMainViewHolder = new MainViewHolder(activity);
		mDelegate = delegate;
		mContext = activity;
	}

	@OnClick(R.id.imgview)
	void onImageClick()
	{
		mDelegate.startPhotoPicker();
	}

	@OnClick({R.id.rotateLeftButton, R.id.rotateRightButton})
	void rotateBitmap(View view)
	{
		if (mImageModel == null || mImageModel.getBitmap() == null)
		{
			return;
		}
		float degrees = view.getId() == R.id.rotateRightButton
		                ? 90
		                : -90;
		Bitmap bitmap = ImageUtils.rotate(mImageModel.getBitmap(), degrees);
		mImageModel.setBitmap(bitmap);
		mMainViewHolder.setImage(bitmap);
		startFaceDetection();
	}

	@OnClick(R.id.button)
	void onMainButtonClick()
	{
		if (mImageModel == null)
		{
			return;
		}
		anonymizePhoto();
	}

	private void updateModel(@Nullable ImageModel model)
	{
		mImageModel = model;
		if (model != null)
		{
			Bitmap bitmap = model.getBitmap();
			mMainViewHolder.setImage(bitmap);
			mMainViewHolder.setButtonsEnabled(bitmap != null);
		}
	}

	private void showLoadingDialog()
	{
		mDialog = ProgressDialog.show(mContext, "", mContext.getString(R.string.loading), true);
	}

	private void hideLoadingDialog()
	{
		if (mDialog != null)
		{
			mDialog.cancel();
		}
	}

	@Override
	public void onFaceDetected(int count)
	{
		if (mImageModel != null)
		{
			mImageModel.setCount(count);
		}
		mMainViewHolder.setCountLabel(count);
		hideLoadingDialog();
	}

	@Override
	public void onAnonymizationSuccess(@NonNull Bitmap bitmap)
	{
		if (mImageModel != null)
		{
			mImageModel.setBitmap(bitmap);
			updateModel(mImageModel);
		}
		hideLoadingDialog();
	}

	public void destroy()
	{
		if (mCurrentTask != null)
		{
			mCurrentTask.cancel(true);
			mCurrentTask = null;
		}
		hideLoadingDialog();
	}

	private void startFaceDetection()
	{
		startTask(new FaceDetectionTask(mContext, this));
	}

	private void anonymizePhoto()
	{
		startTask(new AnonymizerTask(mContext, this));
	}

	private void startTask(@NonNull AsyncTask<Bitmap, ?, ?> task)
	{
		if (mImageModel != null)
		{
			showLoadingDialog();
			mCurrentTask = task.execute(mImageModel.getBitmap());
		}
	}

	public void save(@NonNull Bundle outState)
	{
		if (mImageModel != null)
		{
			mImageModel.save(outState);
		}
	}

	public void restore(@NonNull Context context, @NonNull Bundle savedInstanceState)
	{
		updateModel(ImageModel.restore(context, savedInstanceState));
		if (mImageModel != null)
		{
			mMainViewHolder.setCountLabel(mImageModel.getCount());
		}
	}

	public void setNewImageUri(@Nullable Uri image)
	{
		if (image != null)
		{
			updateModel(new ImageModel(mContext, image));
			startFaceDetection();
		}
	}

	public interface Delegate
	{
		void startPhotoPicker();
	}
}

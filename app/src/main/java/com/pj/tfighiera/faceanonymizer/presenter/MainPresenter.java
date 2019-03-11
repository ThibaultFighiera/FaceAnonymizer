package com.pj.tfighiera.faceanonymizer.presenter;

import com.pj.tfighiera.faceanonymizer.R;
import com.pj.tfighiera.faceanonymizer.helpers.ImageUtils;
import com.pj.tfighiera.faceanonymizer.model.ImageModel;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.AnonymizerCallable;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.FaceCountCallable;
import com.pj.tfighiera.faceanonymizer.view.MainViewHolder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Manage View Model and anonymizing tasks
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version 1.0
 */
public class MainPresenter
{
	@Nullable
	private ProgressDialog mDialog;
	@NonNull
	private final MainViewHolder mMainViewHolder;
	@NonNull
	private Delegate mDelegate;
	@Nullable
	private ImageModel mImageModel;
	@NonNull
	private final Context mContext;
	@Nullable
	private Disposable mDisposableTask;

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

	public void destroy()
	{
		if (mDisposableTask != null && !mDisposableTask.isDisposed())
		{
			mDisposableTask.dispose();
		}
		hideLoadingDialog();
	}

	private void startFaceDetection()
	{
		if (mImageModel != null)
		{
			showLoadingDialog();
			mDisposableTask = Single.fromCallable(new FaceCountCallable(mContext, mImageModel.getBitmap()))
			                        .subscribeOn(Schedulers.computation())
			                        .observeOn(AndroidSchedulers.mainThread())
			                        .subscribe(count -> {
										mImageModel.setCount(count);
										mMainViewHolder.setCountLabel(count);
										hideLoadingDialog();
									});
		}
	}

	private void anonymizePhoto()
	{
		if (mImageModel != null && mImageModel.getBitmap() != null)
		{
			showLoadingDialog();
			mDisposableTask = Single.fromCallable(new AnonymizerCallable(mContext, mImageModel.getBitmap()))
			                        .subscribeOn(Schedulers.computation())
			                        .observeOn(AndroidSchedulers.mainThread())
			                        .subscribe(bitmap -> {
										mImageModel.setBitmap(bitmap);
										updateModel(mImageModel);
										hideLoadingDialog();
									});
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

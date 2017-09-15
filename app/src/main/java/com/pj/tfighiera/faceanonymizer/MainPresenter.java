package com.pj.tfighiera.faceanonymizer;

import com.pj.tfighiera.faceanonymizer.model.ImageModel;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.AnonymizerTask;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.FaceDetectionTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * //TODO : Add a class header comments
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version //TODO : add version
 */
public class MainPresenter implements FaceDetectionTask.FaceDetectionDelegate, AnonymizerTask.AnonymizerDelegate
{
	@BindView(R.id.imgview)
	ImageView mImageView;
	@BindView(R.id.title)
	TextView mTitleView;
	@Nullable
	@BindView(R.id.rotateLeftButton)
	ImageView mImageLeftRotate;
	@Nullable
	@BindView(R.id.rotateRightButton)
	ImageView mImageRigthRotate;
	@BindView(R.id.button)
	Button mProcessButton;
	@Nullable
	private ProgressDialog mDialog;
	private Delegate mDelegate;
	@Nullable
	private ImageModel mImageModel;

	public MainPresenter(@NonNull Activity activity, @NonNull Delegate delegate)
	{
		ButterKnife.bind(this, activity);
		mDelegate = delegate;
	}

	private void setCountView(int count)
	{
		mTitleView.setText(mTitleView.getResources()
		                             .getQuantityString(R.plurals.faces, count, count));
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
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		Bitmap bitmap = Bitmap.createBitmap(mImageModel.getBitmap(), 0, 0, mImageModel.getWidth(), mImageModel.getHeight(), matrix, true);
		mImageModel.setBitmap(bitmap);
		mImageView.setImageBitmap(bitmap);
		mDelegate.startDetectionTask(mImageModel);
	}

	@OnClick(R.id.button)
	void onMainButtonClick()
	{
		if (mImageModel == null)
		{
			return;
		}
		showLoadingDialog();
		mDelegate.anonymizePhoto(mImageModel);
	}

	void updateModel(@Nullable ImageModel model)
	{
		mImageModel = model;
		if (model != null)
		{
			Bitmap bitmap = model.getBitmap();
			setImage(bitmap);
			setCountView(mImageModel.getCount());
			mProcessButton.setEnabled(bitmap != null);
			if (mImageLeftRotate != null && mImageRigthRotate != null)
			{
				mImageRigthRotate.setEnabled(bitmap != null);
				mImageLeftRotate.setEnabled(bitmap != null);
			}
		}
	}

	private void showLoadingDialog()
	{
		Context context = mImageView.getContext();
		mDialog = ProgressDialog.show(context, "", context.getString(R.string.loading), true);
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
		setCountView(count);
		mImageModel.setCount(count);
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

	private void setImage(@Nullable Bitmap bitmap)
	{
		mImageView.setImageBitmap(bitmap);
	}

	public void destroy()
	{
		hideLoadingDialog();
	}

	public void OnStartFaceDetection()
	{
		showLoadingDialog();
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
	}

	interface Delegate
	{
		void startPhotoPicker();

		void startDetectionTask(@NonNull ImageModel bitmap);

		void anonymizePhoto(@NonNull ImageModel bitmap);
	}
}

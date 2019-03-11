package com.pj.tfighiera.faceanonymizer.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pj.tfighiera.faceanonymizer.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Manage View components
 *
 * created on 17/12/2017
 *
 * @author thibaultfighiera
 * @version 1.0
 */
public class MainViewHolder
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

	public MainViewHolder(@NonNull Activity activity)
	{
		ButterKnife.bind(this, activity);
	}

	public void setCountLabel(int count)
	{
		mTitleView.setText(mTitleView.getResources()
		                             .getQuantityString(R.plurals.faces, count, count));
	}

	public void setImage(@Nullable Bitmap bitmap)
	{
		mImageView.setImageBitmap(bitmap);
	}

	public void setButtonsEnabled(boolean enabled)
	{
		mProcessButton.setEnabled(enabled);
		if (mImageLeftRotate != null && mImageRigthRotate != null)
		{
			mImageRigthRotate.setEnabled(enabled);
			mImageLeftRotate.setEnabled(enabled);
		}
	}
}

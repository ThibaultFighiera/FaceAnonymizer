package com.pj.tfighiera.faceanonymizer;

import com.pj.tfighiera.faceanonymizer.tasks.AnonymizerTask;
import com.pj.tfighiera.faceanonymizer.tasks.FaceDetectionTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AnonymizerTask.AnonymizerDelegate, FaceDetectionTask.FaceDetectionDelegate
{
	private static final int REQUEST_IMAGE_CAPTURE = 1234;
	private static final String SAVED_PHOTO = "saved-photo";
	public static final String SAVED_FACE_COUNT = "saved-face-count";
	@Nullable
	Bitmap mBitmap;
	@Nullable
	AnonymizerTask mAnonymizerTask;
	@NonNull
	private ImageView mImageView;
	@NonNull
	private TextView mTitleView;
	@NonNull
	private Button mProcessButton;
	private int mCount;
	@Nullable
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		mTitleView = (TextView) findViewById(R.id.title);
		mImageView = (ImageView) findViewById(R.id.imgview);
		mImageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getPackageManager()) != null)
				{
					startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
				}
			}
		});

		mProcessButton = (Button) findViewById(R.id.button);
		mProcessButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mBitmap != null)
				{
					showLoadingDialog();
					mAnonymizerTask = new AnonymizerTask(MainActivity.this, MainActivity.this);
					mAnonymizerTask.execute(mBitmap);
				}
			}
		});

		if (savedInstanceState != null)
		{
			restoreValues(savedInstanceState);

			if (mBitmap != null)
			{
				mImageView.setImageBitmap(mBitmap);
				setCountView(mCount);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putParcelable(SAVED_PHOTO, mBitmap);
		outState.putInt(SAVED_FACE_COUNT, mCount);
	}

	private void restoreValues(Bundle savedInstanceState)
	{
		setBitmap((Bitmap) savedInstanceState.getParcelable(SAVED_PHOTO));
		mCount = savedInstanceState.getInt(SAVED_FACE_COUNT);
	}

	private void cancelTask()
	{
		if (mAnonymizerTask != null)
		{
			mAnonymizerTask.cancel(true);
			mAnonymizerTask = null;
		}
	}

	@Override
	protected void onDestroy()
	{
		cancelTask();
		stopLoadingDialog();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");

			if (imageBitmap != null)
			{
				setBitmap(imageBitmap);
				mImageView.setImageBitmap(imageBitmap);
				showLoadingDialog();
				new FaceDetectionTask(MainActivity.this, MainActivity.this).execute(imageBitmap);
			}
		}
	}

	@Override
	public void onAnonymizationSuccess(@NonNull Bitmap mask)
	{
		mImageView.setImageDrawable(new BitmapDrawable(getResources(), mask));
		stopLoadingDialog();
	}

	@Override
	public void onFaceDetected(int count)
	{
		mCount = count;
		setCountView(count);
		stopLoadingDialog();
	}

	private void setCountView(int count)
	{
		mTitleView.setText(getResources().getQuantityString(R.plurals.faces, count, count));
	}

	private void setBitmap(@Nullable Bitmap bitmap)
	{
		mBitmap = bitmap;
		mProcessButton.setEnabled(bitmap != null);
	}

	private void showLoadingDialog()
	{
		mDialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading), true);
	}

	private void stopLoadingDialog()
	{
		if (mDialog != null)
		{
			mDialog.cancel();
		}
	}
}

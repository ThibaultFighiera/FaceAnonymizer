package com.pj.tfighiera.faceanonymizer;

import com.pj.tfighiera.faceanonymizer.model.ImageModel;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.AnonymizerTask;
import com.pj.tfighiera.faceanonymizer.presenter.tasks.FaceDetectionTask;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements MainPresenter.Delegate
{
	private static final int REQUEST_IMAGE_CAPTURE = 1234;
	@Nullable
	AnonymizerTask mAnonymizerTask;
	@NonNull
	private MainPresenter mMainPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		mMainPresenter = new MainPresenter(this, this);
		if (savedInstanceState != null)
		{
			restoreValues(savedInstanceState);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		mMainPresenter.save(outState);
	}

	private void restoreValues(Bundle savedInstanceState)
	{
		mMainPresenter.restore(savedInstanceState);
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
		mMainPresenter.destroy();
		cancelTask();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
		{
			Uri imageUri = data.getData();
			ImageModel model = extractModel(imageUri);

			if (model != null && model.getBitmap() != null)
			{
				mMainPresenter.updateModel(model);
				mMainPresenter.OnStartFaceDetection();
				new FaceDetectionTask(MainActivity.this, mMainPresenter).execute(model.getBitmap());
			}
		}
	}

	@Nullable
	private ImageModel extractModel(@Nullable Uri imageUri)
	{
		if (imageUri != null)
		{
			try
			{
				InputStream imageStream = getContentResolver().openInputStream(imageUri);
				return new ImageModel(imageUri, BitmapFactory.decodeStream(imageStream));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void startPhotoPicker()
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_IMAGE_CAPTURE);
	}

	@Override
	public void startDetectionTask(@NonNull ImageModel model)
	{
		new FaceDetectionTask(this, mMainPresenter).execute(model.getBitmap());
	}

	@Override
	public void anonymizePhoto(@NonNull ImageModel model)
	{
		mAnonymizerTask = new AnonymizerTask(MainActivity.this, mMainPresenter);
		mAnonymizerTask.execute(model.getBitmap());
	}
}

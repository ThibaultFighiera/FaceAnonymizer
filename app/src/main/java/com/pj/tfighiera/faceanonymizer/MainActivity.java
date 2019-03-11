package com.pj.tfighiera.faceanonymizer;

import com.pj.tfighiera.faceanonymizer.presenter.MainPresenter;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements MainPresenter.Delegate
{
	private static final int REQUEST_IMAGE_CAPTURE = 1234;
	private MainPresenter mMainPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		mMainPresenter = new MainPresenter(this, this);
		restoreValues(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		mMainPresenter.save(outState);
	}

	private void restoreValues(@Nullable Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			mMainPresenter.restore(this, savedInstanceState);
		}
	}

	@Override
	protected void onDestroy()
	{
		mMainPresenter.destroy();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
		{
			mMainPresenter.setNewImageUri(data.getData());
		}
	}

	@Override
	public void startPhotoPicker()
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_IMAGE_CAPTURE);
	}
}

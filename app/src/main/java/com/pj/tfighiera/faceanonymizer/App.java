package com.pj.tfighiera.faceanonymizer;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;

public class App extends Application
{
	private static App instance;

	@Override
	public void onCreate()
	{
		instance = this;
		super.onCreate();
	}

	@NonNull
	public static Context getContext()
	{
		return instance;
	}
}

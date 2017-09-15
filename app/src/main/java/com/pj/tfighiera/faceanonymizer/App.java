package com.pj.tfighiera.faceanonymizer;

import android.app.Application;
import android.content.Context;

/**
 * //TODO : Add a class header comments
 *
 * created on 15/09/2017
 *
 * @author thibaultfighiera
 * @version //TODO : add version
 */
public class App extends Application
{
	private static App instance;

	public static App getInstance()
	{
		return instance;
	}

	public static Context getContext()
	{
		return instance;
		// or return instance.getApplicationContext();
	}

	@Override
	public void onCreate()
	{
		instance = this;
		super.onCreate();
	}
}

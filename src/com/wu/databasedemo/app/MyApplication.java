package com.wu.databasedemo.app;

import android.app.Application;

public class MyApplication extends Application {

	public static Application Instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Instance = this;
	}
	
}

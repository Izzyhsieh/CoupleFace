package com.coupleface;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity 
{
	private long splashDelay = 2000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				finish();
				Intent LoginIntent = new Intent().setClass(SplashActivity.this, LoginActivity.class);
			    Bundle bundle = new Bundle();
				bundle.putString("isInit", "true");
				LoginIntent.putExtras(bundle);
				startActivity(LoginIntent);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, splashDelay);		
	}
}
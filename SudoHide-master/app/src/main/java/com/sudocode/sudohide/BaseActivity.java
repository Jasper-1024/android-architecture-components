package com.sudocode.sudohide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

@SuppressLint("Registered")
public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		boolean isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
		setTheme(isNightMode ? R.style.DarkTheme : R.style.LightTheme);
	}

}

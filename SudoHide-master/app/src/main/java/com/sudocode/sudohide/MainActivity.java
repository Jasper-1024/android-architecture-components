package com.sudocode.sudohide;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sudocode.sudohide.Adapters.AppListGetter;
import com.sudocode.sudohide.Adapters.MainAdapter;
import com.sudocode.sudohide.Adapters.ShowConfigurationAdapter;

import java.io.File;

public class MainActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static SharedPreferences pref;
	private static final String preferencesFileName = BuildConfig.APPLICATION_ID + "_preferences";
	private final Handler handler = new Handler();
	private ListView listView;
	private MainAdapter mAdapter;
	public boolean isXposedActive = false;
	FileObserver mFileObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pref = getSharedPreferences(preferencesFileName, MODE_PRIVATE);
		mAdapter = new MainAdapter(this, pref.getBoolean(Constants.KEY_SHOW_SYSTEM_APP, false));

		pref.registerOnSharedPreferenceChangeListener(this);
		fixPermissionsAsync();

		try {
			mFileObserver = new FileObserver(getApplicationContext().getFilesDir().getParentFile() + "/shared_prefs", FileObserver.CLOSE_WRITE) {
				@Override
				public void onEvent(int event, String path) {
					fixPermissionsAsync();
				}
			};
			mFileObserver.startWatching();
		} catch (Throwable t) {
			Log.e("prefs", "Failed to start FileObserver!");
		}

		checkIfXposedIsActive();
		EditText inputSearch = findViewById(R.id.searchInput);

		assert inputSearch != null;
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				MainActivity.this.mAdapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

			@Override
			public void afterTextChanged(Editable arg0) {}
		});

		listView = findViewById(R.id.mListView);
		assert listView != null;
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MainActivity.this, AppHideConfigurationActivity.class);
				intent.putExtra(Constants.KEY_PACKAGE_NAME, mAdapter.getKey(position));
				startActivity(intent);
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final String pkgName = mAdapter.getKey(position);

				View settingsDisplay = getLayoutInflater().inflate(R.layout.settingsdisplay, parent, false);
				ListView sub_listView = settingsDisplay.findViewById(R.id.settingsDisplayListViewID);
				ShowConfigurationAdapter subListAdapter = new ShowConfigurationAdapter(MainActivity.this, pkgName);
				sub_listView.setAdapter(subListAdapter);

				new AlertDialog.Builder(MainActivity.this).setTitle(R.string.title_hide_app).setView(settingsDisplay).show();
				return true;
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		mAdapter.notifyDataSetChanged();
	}

	@SuppressLint({"SetWorldReadable", "SetWorldWritable"})
	public void fixPermissionsAsync() {
		AsyncTask.execute(() -> {
			try { Thread.sleep(500); } catch (Throwable t) {}
			File pkgFolder = getApplicationContext().getFilesDir().getParentFile();
			if (pkgFolder.exists()) {
				pkgFolder.setExecutable(true, false);
				pkgFolder.setReadable(true, false);
				//pkgFolder.setWritable(true, false);
				File sharedPrefsFolder = new File(pkgFolder.getAbsolutePath() + "/shared_prefs");
				if (sharedPrefsFolder.exists()) {
					sharedPrefsFolder.setExecutable(true, false);
					sharedPrefsFolder.setReadable(true, false);
					//sharedPrefsFolder.setWritable(true, false);
					File f = new File(sharedPrefsFolder.getAbsolutePath() + "/" + preferencesFileName + ".xml");
					if (f.exists()) {
						f.setReadable(true, false);
						f.setExecutable(true, false);
						//f.setWritable(true, false);
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
		pref.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		pref.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onDestroy() {
		mFileObserver.stopWatching();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem showSystemApps = menu.findItem(R.id.action_show_system_app);
		MenuItem showPackageName = menu.findItem(R.id.action_show_package_name);

		showSystemApps.setChecked(pref.getBoolean(Constants.KEY_SHOW_SYSTEM_APP, false));
		showPackageName.setChecked(pref.getBoolean(Constants.KEY_SHOW_PACKAGE_NAME, false));
		refresh(showSystemApps.isChecked());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_restart_launcher:
				handler.post(this::restartLauncher);
				return true;
			case R.id.action_show_system_app:
				boolean showSystemApps = !item.isChecked();
				item.setChecked(showSystemApps);
				pref.edit().putBoolean(Constants.KEY_SHOW_SYSTEM_APP, showSystemApps).apply();
				refresh(showSystemApps);
				return true;
			case R.id.action_show_package_name:
				boolean showPackageName = !item.isChecked();
				item.setChecked(showPackageName);
				pref.edit().putBoolean(Constants.KEY_SHOW_PACKAGE_NAME, showPackageName).apply();
				mAdapter.notifyDataSetChanged();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void restartLauncher() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PackageManager packageManager = getPackageManager();
		ResolveInfo resolveInfo = packageManager.resolveActivity(startMain, PackageManager.MATCH_DEFAULT_ONLY);
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(resolveInfo.activityInfo.packageName);
		try {
			packageManager.getApplicationLabel(packageManager.getApplicationInfo(resolveInfo.activityInfo.packageName,0));
			Toast.makeText(this, "\uD83D\uDC80 " + resolveInfo.activityInfo.packageName, Toast.LENGTH_LONG).show();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void refresh(boolean showSystemApp) {
		mAdapter = new MainAdapter(this, showSystemApp);
		listView.setAdapter(mAdapter);
	}

	private void checkIfXposedIsActive() {
		if (isXposedActive) return;

		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		alertDialog.setTitle(R.string.app_name);
		alertDialog.setMessage(getString(R.string.sudoHide_module_not_active));
		alertDialog.setCancelable(false);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.exit_app), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AppListGetter.getInstance(MainActivity.this).cancel(true);
				finish();
			}
		});
		alertDialog.show();
	}
}
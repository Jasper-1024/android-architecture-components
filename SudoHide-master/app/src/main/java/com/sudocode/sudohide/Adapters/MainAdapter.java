package com.sudocode.sudohide.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudocode.sudohide.BitmapCachedLoader;
import com.sudocode.sudohide.Constants;
import com.sudocode.sudohide.R;

import java.util.Set;

public class MainAdapter extends AppListAdapter {

	private final Set<String> mHidingConfigurationKeySet;

	public MainAdapter(Context context, boolean showSystemApps) {
		super(context, showSystemApps);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mHidingConfigurationKeySet = PreferenceManager.getDefaultSharedPreferences(mContext).getAll().keySet();
	}

	public String getKey(int position) {
		return mDisplayItems.get(position).getKey();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = mInflater.inflate(R.layout.list_item, parent, false);

		final TextView title = convertView.findViewById(R.id.app_name);
		final ImageView icon = convertView.findViewById(R.id.app_icon);
		final TextView subTitle = convertView.findViewById(R.id.package_name);

		final String sTitle = mDisplayItems.get(position).getTitle();
		final String key = mDisplayItems.get(position).getKey();

		icon.setTag(position);
		icon.setImageResource(R.mipmap.ic_default);
		Bitmap bmp = BitmapCachedLoader.memoryCache.get(key);
		if (bmp == null)
			(new BitmapCachedLoader(icon, mDisplayItems.get(position), getContext())).executeOnExecutor(this.mPool);
		else
			icon.setImageBitmap(bmp);

		title.setText(sTitle);
		subTitle.setText(key);
		subTitle.setVisibility(PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Constants.KEY_SHOW_PACKAGE_NAME, false) ? View.VISIBLE : View.GONE);

		TypedArray a = mContext.obtainStyledAttributes(new TypedValue().data, new int[]{ android.R.attr.textColorPrimary, android.R.attr.textColorSecondary, android.R.attr.colorAccent, android.R.attr.colorBackground });
		title.setTextColor(a.getColor(0, Color.BLACK));
		subTitle.setTextColor(a.getColor(1, Color.DKGRAY));
		if (appIsHidden(key)) {
			int color = a.getColor(2, Color.rgb(200, 44, 44));
			subTitle.setTextColor(color);
			title.setTextColor(color);
		}
		convertView.setBackgroundColor(a.getColor(3, Color.WHITE));
		a.recycle();

		icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = mContext.getPackageManager().getLaunchIntentForPackage(key);
				if (it == null) return;
				it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(it);
			}
		});

		return convertView;
	}

	private boolean appIsHidden(String packageName) {
		for (String key : mHidingConfigurationKeySet)
		if (key.endsWith(packageName)) return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(key, false);
		return false;
	}
}
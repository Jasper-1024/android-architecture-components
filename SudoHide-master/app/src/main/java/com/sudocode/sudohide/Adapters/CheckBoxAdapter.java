package com.sudocode.sudohide.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudocode.sudohide.BitmapCachedLoader;
import com.sudocode.sudohide.Constants;
import com.sudocode.sudohide.R;

import java.util.Map;
import java.util.TreeMap;

public class CheckBoxAdapter extends AppListAdapter {

	private final SharedPreferences pref;
	private final String currentPkgName;
	private final Map<String, Boolean> changedItems;

	public CheckBoxAdapter(Context context, String pkgName, boolean showSystemApps) {
		super(context, showSystemApps);
		pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		currentPkgName = pkgName;
		changedItems = new TreeMap<>();
	}

	public Map<String, Boolean> getChangedItems() {
		return changedItems;
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
		TypedArray a = mContext.obtainStyledAttributes((new TypedValue()).data, new int[]{ android.R.attr.textColorPrimary });
		title.setTextColor(a.getColor(0, Color.BLACK));
		a.recycle();

		subTitle.setText(key);
		subTitle.setVisibility(PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Constants.KEY_SHOW_PACKAGE_NAME, false) ? View.VISIBLE : View.GONE);

		final String pref_key = key + ":" + currentPkgName;
		final CheckBox checkBox = convertView.findViewById(android.R.id.checkbox);
		checkBox.setVisibility(View.VISIBLE);
		if (changedItems.containsKey(pref_key)) {
			Boolean state = changedItems.get(pref_key);
			checkBox.setChecked(state == null ? false : state);
		} else {
			checkBox.setChecked(pref.getBoolean(pref_key, false));
		}

		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addValue(((CheckBox)v).isChecked(), pref_key);
			}
		});

		return convertView;
	}

	private void addValue(boolean value, String pref_key) {
		if (changedItems.containsKey(pref_key)) {
			changedItems.remove(pref_key);
		} else {
			changedItems.put(pref_key, value);
		}
	}

}

package com.sudocode.sudohide.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudocode.sudohide.ApplicationData;
import com.sudocode.sudohide.BitmapCachedLoader;
import com.sudocode.sudohide.Constants;
import com.sudocode.sudohide.R;

import java.util.ArrayList;
import java.util.List;

public class ShowConfigurationAdapter extends AppListAdapter {

	private final Context mContext;
	private final LayoutInflater mInflater;
	private final String mPackageName;
	private final SharedPreferences pref;

	public ShowConfigurationAdapter(Context context, String packageName) {
		super(context, true);
		mContext = context;
		pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mInflater = LayoutInflater.from(context);
		mPackageName = packageName;
		populateDisplayList();
	}

	private void populateDisplayList() {
		List<ApplicationData> appList = new ArrayList<>();
		for (ApplicationData app : mDisplayItems) {
			final String pref_key = app.getKey() + ":" + mPackageName;
			if (pref.getBoolean(pref_key, false)) {
				appList.add(app);
			}
		}
		mDisplayItems = appList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = mInflater.inflate(R.layout.list_item, parent, false);

		final TextView title = convertView.findViewById(R.id.app_name);
		final TextView subTitle = convertView.findViewById(R.id.package_name);
		final ImageView icon = convertView.findViewById(R.id.app_icon);

		final String sTitle = mDisplayItems.get(position).getTitle();
		final String sSubTitle = mDisplayItems.get(position).getKey();

		icon.setTag(position);
		icon.setImageResource(R.mipmap.ic_default);
		Bitmap bmp = BitmapCachedLoader.memoryCache.get(sSubTitle);
		if (bmp == null)
			(new BitmapCachedLoader(icon, mDisplayItems.get(position), getContext())).executeOnExecutor(this.mPool);
		else
			icon.setImageBitmap(bmp);

		title.setText(sTitle);
		subTitle.setText(sTitle);
		subTitle.setVisibility(PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Constants.KEY_SHOW_PACKAGE_NAME, false) ? View.VISIBLE : View.GONE);

		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return mDisplayItems.get(position).hashCode();
	}

	@Override
	public int getCount() {
		return mDisplayItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mDisplayItems.get(position);
	}

}
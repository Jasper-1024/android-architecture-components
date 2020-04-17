package com.sudocode.sudohide;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

@SuppressLint("StaticFieldLeak")
public class BitmapCachedLoader extends AsyncTask<Void, Void, Bitmap> {

	public static LruCache<String, Bitmap> memoryCache = new LruCache<String, Bitmap>((int)(Runtime.getRuntime().maxMemory() / 1024) / 2) {
		@Override
		protected int sizeOf(String key, Bitmap icon) {
			if (icon != null)
				return icon.getAllocationByteCount() / 1024;
			else
				return 130 * 130 * 4 / 1024;
		}
	};

	private WeakReference<Object> targetRef;
	private WeakReference<Object> appInfo;
	private Context ctx;
	private int theTag = -1;
	
	public BitmapCachedLoader(Object target, Object info, Context context) {
		targetRef = new WeakReference<Object>(target);
		appInfo = new WeakReference<Object>(info);
		ctx = context.getApplicationContext();
		Object tag = ((ImageView)target).getTag();
		if (tag != null) theTag = (Integer)tag;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Drawable icon = null;
		int newIconSize = ctx.getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);

		ApplicationData ad = ((ApplicationData)appInfo.get());
		if (ad != null) try {
			if (ad.getKey() == null || ad.getKey().equals("")) return null;
			if (ctx.getPackageManager().getApplicationInfo(ad.getKey(), 0).icon != 0)
			icon = ctx.getPackageManager().getApplicationIcon(ad.getKey());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (icon == null) return null;

		Bitmap bmp = Bitmap.createBitmap(newIconSize, newIconSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		icon.setBounds(0, 0, newIconSize, newIconSize);
		icon.draw(canvas);

		//Log.e("mem_left", String.valueOf(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()));
		if (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() > 8 * 1024 * 1024)
			memoryCache.put(ad.getKey(), bmp);
		else
			Runtime.getRuntime().gc();

		return bmp;
	}
	
	@Override
	protected void onPostExecute(Bitmap bmp) {
		if (targetRef != null && targetRef.get() != null && bmp != null) {
			Object tag = ((ImageView)targetRef.get()).getTag();
			if (tag != null && theTag == (Integer)tag) {
				ImageView itemIcon = ((ImageView)targetRef.get());
				if (itemIcon != null) itemIcon.setImageBitmap(bmp);
			}
		}
		//Log.e("mem_used", String.valueOf(Helpers.memoryCache.size()) + " KB / " + String.valueOf(Runtime.getRuntime().totalMemory() / 1024) + " KB");
	}
}

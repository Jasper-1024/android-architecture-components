package com.sudocode.sudoHideModule;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;

import com.sudocode.sudohide.BuildConfig;
import com.sudocode.sudohide.Constants;
import com.sudocode.sudohide.PrefMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {

	private static final String X_SUDOHIDE_TAG = "[XSudohide] ";
	private static PrefMap<String, Object> mPrefs = new PrefMap<String, Object>();

	private static void logDebug(String msg) {
		if (BuildConfig.DEBUG) XposedBridge.log(X_SUDOHIDE_TAG + msg);
	}

	@Override
	public void initZygote(StartupParam startupParam) {
		if (mPrefs.size() == 0) {
			XSharedPreferences pref = null;
			try {

				pref = new XSharedPreferences(BuildConfig.APPLICATION_ID);
//				pref.makeWorldReadable();
			} catch (Throwable t) {
				XposedBridge.log(t);
			}

			if (pref == null || pref.getAll().size() == 0)
				XposedBridge.log(X_SUDOHIDE_TAG + "Cannot read module's SharedPreferences! " + android.os.Process.myUid());
			else
				mPrefs.putAll(pref.getAll());
		}

		try {
			Class<?> clsPMS = XposedHelpers.findClass("android.app.ApplicationPackageManager", null);

			XposedBridge.hookAllMethods(clsPMS, "getApplicationInfo", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					modifyHookedMethodArguments(param);
				}
			});

			XposedBridge.hookAllMethods(clsPMS, "getPackageInfo", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					modifyHookedMethodArguments(param);
				}
			});

			XposedBridge.hookAllMethods(clsPMS, "getInstalledApplications", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					modifyHookedMethodResult(param, new ApplicationInfoData());
				}
			});

			XposedBridge.hookAllMethods(clsPMS, "getInstalledPackages", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					modifyHookedMethodResult(param, new PackageInfoData());
				}
			});

			XposedBridge.hookAllMethods(clsPMS, "getPackagesForUid", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					modifyHookedMethodResult(param, new PackageNameStringData());
				}
			});

			XposedBridge.hookAllMethods(clsPMS, "queryIntentActivities", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					modifyHookedMethodResult(param, new ResolveInfoData());
				}
			});

			XposedBridge.hookAllMethods(clsPMS, "queryIntentActivityOptions", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					super.afterHookedMethod(param);
					modifyHookedMethodResult(param, new ResolveInfoData());

				}
			});
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	private String getCallingName(Object thiz) {
		int uid = Binder.getCallingUid();
		return (String)XposedHelpers.callMethod(thiz, "getNameForUid", uid);
	}

	private boolean shouldBlock(Object thiz, String callingName, String queryName) {
		String key = callingName + ":" + queryName;
		if (mPrefs.getBoolean(key)) {
			logDebug(key + " true");
			return true;
		}

		String key_hide_from_system = queryName + Constants.KEY_HIDE_FROM_SYSTEM;
		if (mPrefs.getBoolean(key_hide_from_system)) {
			// block system processes like android.uid.systemui:10015
			if (callingName.contains(":")) {
				logDebug(key + " true");
				return true;
			}

			Context mContext = (Context)XposedHelpers.getObjectField(thiz, "mContext");
			Object mPM = XposedHelpers.getObjectField(thiz, "mPM");
			if (mContext == null || mPM == null) return false;
			ApplicationInfo info = (ApplicationInfo)XposedHelpers.callMethod(mPM, "getApplicationInfo", callingName, 0, XposedHelpers.callMethod(mContext, "getUserId"));
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				logDebug(key + " true");
				return true;
			}
		}

		//logDebug(key + " false");
		return false;
	}

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
		if (BuildConfig.APPLICATION_ID.equals(lpparam.packageName))
		XposedHelpers.findAndHookMethod(BuildConfig.APPLICATION_ID + ".MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				XposedHelpers.setBooleanField(param.thisObject, "isXposedActive", true);
			}
		});
	}

	private void modifyHookedMethodArguments(XC_MethodHook.MethodHookParam param) {
		try {
			if (shouldBlock(param.thisObject, getCallingName(param.thisObject), (String)param.args[0]))	param.args[0] = "";
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	private <T> void modifyHookedMethodResult(XC_MethodHook.MethodHookParam param, InfoData<T> infoData) throws Throwable {
		try {
			List<T> mList = infoData.resultToList(param.getResultOrThrowable());
			if (mList == null) return;
			List<T> result = new ArrayList<>();
			for (T info: mList) {
				if (shouldBlock(param.thisObject, getCallingName(param.thisObject), infoData.getPackageName(info)))	continue;
				result.add(info);
			}
			param.setResult(infoData.getResultObject(result));
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}

	private abstract class InfoData<Type> {
		abstract String getPackageName(Type info);

		public Object getResultObject(List<Type> modifiedResult) {
			return modifiedResult;
		}

		@SuppressWarnings("unchecked")
		public List<Type> resultToList(Object result) {
			return (List<Type>)result;
		}
	}

	private class ResolveInfoData extends InfoData<ResolveInfo> {
		ResolveInfoData() {}

		@Override
		public String getPackageName(ResolveInfo info) {
			return info.activityInfo.packageName;
		}
	}

	private class PackageInfoData extends InfoData<PackageInfo> {
		PackageInfoData() {}

		@Override
		public String getPackageName(PackageInfo info) {
			return info.packageName;
		}
	}

	private class ApplicationInfoData extends InfoData<ApplicationInfo> {
		ApplicationInfoData() {}

		@Override
		public String getPackageName(ApplicationInfo info) {
			return info.packageName;
		}
	}

	private class PackageNameStringData extends InfoData<String> {
		PackageNameStringData() {}

		@Override
		public String getPackageName(String info) {
			return info;
		}

		@Override
		public String[] getResultObject(List<String> result) {
			return result.toArray(new String[0]);
		}

		@Override
		public List<String> resultToList(Object result) {
			return result == null ? null : Arrays.asList((String[]) result);
		}
	}

}
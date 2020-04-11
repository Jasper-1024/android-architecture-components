package com.js.xptest

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

class XposedModule : IXposedHookZygoteInit, IXposedHookLoadPackage {
    private val TAG = "Xposed.Test"

    private val prefsFileProt =
        File("/data/user_de/0/com.js.xptest/shared_prefs/com.js.xptest_preferences.xml")

    var pref: XSharedPreferences? = null

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        XposedBridge.log(TAG + "initZygote")

        try {
//            pref = XSharedPreferences(BuildConfig.APPLICATION_ID)
//            pref!!.makeWorldReadable()

            pref = XSharedPreferences(prefsFileProt)

        } catch (t: Throwable) {
            XposedBridge.log("$TAG : $t")
        }
        XposedBridge.log("$TAG : ${pref?.getString("Test","default")}")
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log(TAG + lpparam.packageName)
    }
}
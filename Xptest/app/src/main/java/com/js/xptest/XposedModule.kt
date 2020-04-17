package com.js.xptest

import android.content.Context
import android.os.IBinder
import android.os.WorkSource
import com.js.nowakelock.xposedhook.xptest
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

class XposedModule : IXposedHookZygoteInit, IXposedHookLoadPackage {
    private val TAG = "Xposed.Test"

//    private val prefsFileProt =
//        File("/data/user_de/0/com.js.xptest/shared_prefs/com.js.xptest_preferences.xml")
//
//    var pref: XSharedPreferences? = null

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        XposedBridge.log("$TAG : initZygote")
////        XpUtil.init()
////        log("${com.js.xptest.TAG} : Test ${XpUtil.getFlag("Test")}")
//
//        try {
////            pref = XSharedPreferences(BuildConfig.APPLICATION_ID)
////            pref!!.makeWorldReadable()
////            pref = XSharedPreferences(BuildConfig.APPLICATION_ID)
////            pref!!.makeWorldReadable()
//
////            pref = XSharedPreferences(prefsFileProt)
////            pref!!.makeWorldReadable()
//
////            log("${TAG} : Test ${pref!!.getBoolean("Test",true)}")
//
//        } catch (t: Throwable) {
//            XposedBridge.log("$TAG : $t")
//        }
//
////        XposedBridge.log(
////            "$TAG : 1 ${pref?.getString("Test", "default")}," +
////                    "2 ${pref?.getString("Test2", "default"
////            )}"
////        )
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log(TAG + lpparam.packageName)

        if(lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {

            // don't use YourActivity.class here

            XposedHelpers.findAndHookMethod(
                "${BuildConfig.APPLICATION_ID}.MainActivity", lpparam.classLoader,

                "isModuleActive", XC_MethodReplacement.returnConstant(true)
            )
        }
//        XpUtil.init()
////        xptest.hookWakeLocks(lpparam)
//        try {
////            pref = XSharedPreferences(BuildConfig.APPLICATION_ID)
////            pref!!.makeWorldReadable()
////            pref = XSharedPreferences(BuildConfig.APPLICATION_ID)
////            pref!!.makeWorldReadable()
//
////            pref = XSharedPreferences(prefsFileProt)
////            pref!!.makeWorldReadable()
//
////            log("${TAG} : Test ${pref!!.getBoolean("Test",true)}")
//
//        } catch (t: Throwable) {
//            XposedBridge.log("$TAG : $t")
//        }
//        xptest.hookWakeLocks(lpparam)
//
//        XposedHelpers.findAndHookMethod("com.android.server.power.PowerManagerService",
//            lpparam.classLoader,
//            "acquireWakeLockInternal",
//            IBinder::class.java,
//            Int::class.javaPrimitiveType,
//            String::class.java,//wakeLockName
//            String::class.java,//packageName
//            WorkSource::class.java,
//            String::class.java,
//            Int::class.javaPrimitiveType,
//            Int::class.javaPrimitiveType,
//            object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                override fun beforeHookedMethod(param: MethodHookParam) {
//
//                    XpUtil.init()
//
//                    val lock = param.args[0] as IBinder
//                    val flags = param.args[1] as Int
//                    val wN = param.args[2] as String
//                    val pN = param.args[3] as String
//                    val ws = param.args[4] as WorkSource?
////                    val historyTag = param.args[5] as String
//                    val uId = param.args[6] as Int
//                    val pid = param.args[7] as Int
//
//                    val context = XposedHelpers.getObjectField(
//                        param.thisObject,
//                        "mContext"
//                    ) as Context
//
////                        try {
////                            log(
////                                "$TAG wakeLock: pN = $pN , tag=\"$wN\" , lock= \"${Objects.hashCode(
////                                    lock
////                                )}\"," +
////                                        "flags=0x${Integer.toHexString(flags)} , ws = \"${ws}\", " +
////                                        "uid= $uId , pid = $pid, mypid ${myPid()}"
////                            )
////                            log("$TAG wakeLock: pN = $pN ,mypid ${myPid()}")
////                        } catch (e: Exception) {
////                            log("$TAG acquireWakeLockInternal: err $e")
////                        }
//
////                    pref = XSharedPreferences(prefsFileProt)
////                    pref = XSharedPreferences(prefsFileProt)
//////                    log("${TAG} : Test 1 ${pref!!.getBoolean("Test",true)}")
//                    try {
////                        pref?.reload()
////                        var pref1 = XSharedPreferences(prefsFileProt)
//                        XpUtil.reload()
//                        log("${TAG} : Test 1 ${XpUtil.getFlag("Test")}")
//
//                    } catch (t: Throwable) {
//                        XposedBridge.log("$TAG : Test 1 $t")
//                    }
//
////                        log("$TAG wakeLock: pN = $pN ,wls ${wls.size},iwN")
//                }
//            })
//
//        XposedHelpers.findAndHookMethod("com.android.server.power.PowerManagerService",
//            lpparam.classLoader,
//            "releaseWakeLockInternal",
//            IBinder::class.java,
//            Int::class.javaPrimitiveType,
//            object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                override fun beforeHookedMethod(param: MethodHookParam) {
//                    val context = XposedHelpers.getObjectField(
//                        param.thisObject,
//                        "mContext"
//                    ) as Context
//                    val lock = param.args[0] as IBinder
//
//                    try {
////                        pref?.reload()
////                        var pref2 = XSharedPreferences(prefsFileProt)
//                        XpUtil.reload()
//                        log("${TAG} : Test 2 ${XpUtil.getFlag("Test")} ")
//
//                    } catch (t: Throwable) {
//                        XposedBridge.log("$TAG : Test 2 $t")
//                    }
//                }
//            })

    }
}
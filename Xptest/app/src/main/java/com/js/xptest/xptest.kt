package com.js.nowakelock.xposedhook

import android.content.Context
import android.os.IBinder
import android.os.WorkSource
import com.js.xptest.TAG
import com.js.xptest.XpUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class xptest {
    companion object {

        fun hookWakeLocks(lpparam: XC_LoadPackage.LoadPackageParam) {
            XposedHelpers.findAndHookMethod("com.android.server.power.PowerManagerService",
                lpparam.classLoader,
                "acquireWakeLockInternal",
                IBinder::class.java,
                Int::class.javaPrimitiveType,
                String::class.java,//wakeLockName
                String::class.java,//packageName
                WorkSource::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {

                        XpUtil.init()

                        val lock = param.args[0] as IBinder
                        val flags = param.args[1] as Int
                        val wN = param.args[2] as String
                        val pN = param.args[3] as String
                        val ws = param.args[4] as WorkSource?
//                    val historyTag = param.args[5] as String
                        val uId = param.args[6] as Int
                        val pid = param.args[7] as Int

                        val context = XposedHelpers.getObjectField(
                            param.thisObject,
                            "mContext"
                        ) as Context

//                        try {
//                            log(
//                                "$TAG wakeLock: pN = $pN , tag=\"$wN\" , lock= \"${Objects.hashCode(
//                                    lock
//                                )}\"," +
//                                        "flags=0x${Integer.toHexString(flags)} , ws = \"${ws}\", " +
//                                        "uid= $uId , pid = $pid, mypid ${myPid()}"
//                            )
//                            log("$TAG wakeLock: pN = $pN ,mypid ${myPid()}")
//                        } catch (e: Exception) {
//                            log("$TAG acquireWakeLockInternal: err $e")
//                        }

                        try {
//                        pref?.reload()
//                        var pref1 = XSharedPreferences(prefsFileProt)
                            XpUtil.reload()
                            XposedBridge.log("${TAG} : Test 1 ${XpUtil.getFlag("Test")}")

                        } catch (t: Throwable) {
                            XposedBridge.log("$TAG : Test 1 $t")
                        }


//                        log("$TAG wakeLock: pN = $pN ,wls ${wls.size},iwN")
                    }
                })

            XposedHelpers.findAndHookMethod("com.android.server.power.PowerManagerService",
                lpparam.classLoader,
                "releaseWakeLockInternal",
                IBinder::class.java,
                Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val context = XposedHelpers.getObjectField(
                            param.thisObject,
                            "mContext"
                        ) as Context
                        val lock = param.args[0] as IBinder

                        try {
//                        pref?.reload()
//                        var pref2 = XSharedPreferences(prefsFileProt)
                            XpUtil.reload()
                            XposedBridge.log("${TAG} : Test 2 ${XpUtil.getFlag("Test")} ")

                        } catch (t: Throwable) {
                            XposedBridge.log("$TAG : Test 2 $t")
                        }
                    }
                })
        }


    }

}


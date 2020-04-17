package com.js.xptest

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import java.io.File

val TAG = "Xposed.Test"

fun log(string: String) = XposedBridge.log(string)

class XpUtil() {
    companion object {
        private var pref: XSharedPreferences? = null
        private val prefsFileProt =
            File("/data/user_de/0/${BuildConfig.APPLICATION_ID}/shared_prefs/${BuildConfig.APPLICATION_ID}_preferences.xml")



        fun init() {
            load()
        }

        fun reload(){
            pref?.reload()
        }

        fun getFlag(wN: String): Boolean {
//            pref?.reload()
            if (pref == null) {
                load()
            }
            log("$TAG: $wN getFlag ${pref!!.getBoolean(wN, true)}")
            return pref!!.getBoolean(wN, true)
        }

        fun getRe(pN: String): String? {
            if (pref == null) {
                load()
            }
            return pref!!.getString("${pN}_RE", "")
        }

        fun load() {
            try {
                pref = XSharedPreferences(prefsFileProt)
                log("$TAG ")
//                pref!!.reload()
            } catch (t: Throwable) {
                log("$TAG : XpUtil pref err: $t")
            }
        }

    }
}

package com.js.xptest

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import java.io.File

class BOOT_COMPLETED : BroadcastReceiver() {

    private val preferencesFileName =
        BuildConfig.APPLICATION_ID + "_preferences"

    override fun onReceive(context: Context?, intent: Intent) {
        if (Intent.ACTION_LOCKED_BOOT_COMPLETED == intent.action) {
            if (context != null) {
                fixPermissionsAsync(context)
            }
        }
    }

    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    fun fixPermissionsAsync(context: Context) {
        AsyncTask.execute {
            try {
                Thread.sleep(500)
            } catch (t: Throwable) {
            }
            val pkgFolder = context.filesDir.parentFile
            if (pkgFolder.exists()) {
                pkgFolder.setExecutable(true, false)
                pkgFolder.setReadable(true, false)
                //pkgFolder.setWritable(true, false);
                val sharedPrefsFolder =
                    File(pkgFolder.absolutePath + "/shared_prefs")
                Log.d("Xposed.Test",": Broadcast ${pkgFolder.absolutePath}/shared_prefs")
                if (sharedPrefsFolder.exists()) {
                    sharedPrefsFolder.setExecutable(true, false)
                    sharedPrefsFolder.setReadable(true, false)
                    //sharedPrefsFolder.setWritable(true, false);
                    val f =
                        File(sharedPrefsFolder.absolutePath + "/" + preferencesFileName + ".xml")
                    if (f.exists()) {
                        f.setReadable(true, false)
                        f.setExecutable(true, false)
                        //f.setWritable(true, false);
                    }
                }
            }
        }
    }
}

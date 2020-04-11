package com.js.xptest

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    var pref: SharedPreferences? = null
    private val preferencesFileName =
        BuildConfig.APPLICATION_ID + "_preferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = this.createDeviceProtectedStorageContext()
            .getSharedPreferences(preferencesFileName, MODE_PRIVATE)

        fixPermissionsAsync()

    }

    fun click(view: View) {
        pref?.edit()
            ?.putString("Test", "Test233")
            ?.apply()
    }

    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    fun fixPermissionsAsync() {
        AsyncTask.execute {
            try {
                Thread.sleep(500)
            } catch (t: Throwable) {
            }
            val pkgFolder = this.createDeviceProtectedStorageContext().filesDir.parentFile
            if (pkgFolder.exists()) {
                pkgFolder.setExecutable(true, false)
                pkgFolder.setReadable(true, false)
                //pkgFolder.setWritable(true, false);
                val sharedPrefsFolder =
                    File(pkgFolder.absolutePath + "/shared_prefs")
                Log.d("Xposed.Test",": activity ${pkgFolder.absolutePath}/shared_prefs")
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

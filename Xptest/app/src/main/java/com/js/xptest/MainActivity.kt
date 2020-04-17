package com.js.xptest

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.FileObserver
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class MainActivity : AppCompatActivity() {

//    companion object{
//        fun isModuleActive():Boolean{
//            return false
//        }
//    }


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
            ?.putBoolean("Test", false)
            ?.putString("Te", "TE")
            ?.apply()
        fixPermissionsAsync()
    }
    fun click2(view: View) {
        pref?.edit()
            ?.putBoolean("Test", true)
            ?.putString("Te", "SO")
            ?.apply()
        fixPermissionsAsync()
    }

    fun click3(view: View) {
        val textView:TextView = findViewById(R.id.textView2)
        textView.text = "${isModuleActive()}"
    }
    fun isModuleActive():Boolean{
        return false
    }

//    private fun registerFileObserver() {
//        var mFileObserver = object : FileObserver(
//            this.createDeviceProtectedStorageContext().getDataDir()
//                .toString() + "/shared_prefs",
//            ATTRIB or CLOSE_WRITE
//        ) {
//            override fun onEvent(event: Int, path: String?) {
//                for (l in mFileObserverListeners) {
//                    if (event and ATTRIB != 0) l.onFileAttributesChanged(path)
//                    if (event and CLOSE_WRITE != 0) l.onFileUpdated(path)
//                }
//            }
//        }
//        mFileObserver.startWatching()
//    }

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

package com.kotlin.basicsample_kotlin

import android.app.Application
import com.kotlin.basicsample_kotlin.db.AppDatabase

class BasicApp : Application() {

    private var appExecutors: AppExecutors? = null

    override fun onCreate() {
        super.onCreate()
        appExecutors = AppExecutors()
    }

    fun database() = AppDatabase.getInstance(this, appExecutors!!)
    fun getRepository() = DataRepository.getInstance(database())
}
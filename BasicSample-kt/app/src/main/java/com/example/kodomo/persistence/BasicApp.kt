package com.example.kodomo.persistence

import android.app.Application
import com.example.kodomo.persistence.db.AppDatabase

/**
 * @author linxiaotao
 * 2018/12/27 下午8:33
 */
class BasicApp : Application() {


    private var appExecutors: AppExecutors? = null

    override fun onCreate() {
        super.onCreate()

        appExecutors = AppExecutors()
    }

    fun database() = AppDatabase.getInstance(this, appExecutors!!)

    fun repository() = DataRepository.getInstance(database())
}
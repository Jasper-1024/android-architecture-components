package com.example.kodomo.persistence

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author linxiaotao
 * 2018/12/27 下午4:52
 */
class AppExecutors(private val diskIO: Executor, private val networkIO: Executor, private val mainThread: Executor) {


    constructor() : this(
            Executors.newSingleThreadExecutor(),
            Executors.newFixedThreadPool(3),
            MainThreadExecutor
    )

    fun diskIO() = diskIO

    fun networkIO() = networkIO

    fun mainThread() = mainThread

    private object MainThreadExecutor : Executor {

        val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }

}
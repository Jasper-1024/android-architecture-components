package com.kotlin.basicsample_kotlin

import android.net.Network
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors(
    private var mDiskIO: Executor,
    private var mNetworkIO: Executor,
    private var mMainThread: Executor
) {
    constructor() : this(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(3),
        MainThreadExecutor
    )

    fun DiskIO() = mDiskIO
    fun NetworkIO() = mNetworkIO
    fun MainThread() = mMainThread

    private object MainThreadExecutor : Executor {
        var mainThreadHandler: Handler = Handler(Looper.getMainLooper())
        override fun execute(p0: Runnable) {
            mainThreadHandler.post(p0)
        }
    }
}
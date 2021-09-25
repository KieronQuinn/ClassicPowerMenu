package com.android.systemui.util.extensions

import android.os.HandlerThread
import android.os.Looper
import com.android.systemui.util.concurrency.DelayableExecutor
import com.android.systemui.util.concurrency.ExecutorImpl

fun createBackgroundExecutor(): DelayableExecutor {
    return ExecutorImpl(HandlerThread("controls-background").apply {
        start()
    }.looper)
}

fun createForgroundExecutor(): DelayableExecutor {
    return ExecutorImpl(Looper.getMainLooper())
}
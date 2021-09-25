package com.android.systemui.util.extensions

import android.content.BroadcastReceiver

var BroadcastReceiver.pendingResult: BroadcastReceiver.PendingResult
    get() = this::class.java.getMethod("getPendingResult").invoke(this) as BroadcastReceiver.PendingResult
    set(value) {
        this::class.java.getMethod("setPendingResult").invoke(this, value)
    }

val BroadcastReceiver.sendingUserId: Int
    get() = this::class.java.getMethod("getSendingUserId").invoke(this) as Int
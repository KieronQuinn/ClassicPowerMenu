package com.android.systemui.util.extensions

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.UserHandle

val Context.userId: Int
    get() {
        return this::class.java.getMethod("getUserId").invoke(this) as Int
    }

val Context.user: UserHandle
    get() {
        return this::class.java.getMethod("getUser").invoke(this) as UserHandle
    }

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.registerReceiverCompat(receiver: BroadcastReceiver, intentFilter: IntentFilter) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
    }else{
        registerReceiver(receiver, intentFilter)
    }
}

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.registerReceiverCompat(
    receiver: BroadcastReceiver,
    filter: IntentFilter,
    broadcastPermission: String?,
    scheduler: Handler?
): Intent? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, filter, broadcastPermission, scheduler, RECEIVER_EXPORTED)
    }else{
        registerReceiver(receiver, filter, broadcastPermission, scheduler)
    }
}
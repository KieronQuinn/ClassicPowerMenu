package com.android.systemui.util.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.annotation.AnimRes

interface ServiceRunner {

    fun bindService(context: Context, intent: Intent, serviceConnection: ServiceConnection, flags: Int)
    fun unbindService(context: Context, serviceConnection: ServiceConnection)
    fun startActivity(context: Context, intent: Intent, runAfter: (() -> Unit)? = null)
    fun getIntentForPendingIntent(pendingIntent: PendingIntent, callback: (Intent) -> Unit)
    fun overridePendingTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int)
    fun getCurrentUser(): Int
    fun showPowerMenu()

}
package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun Context.registerReceiver(vararg actions: String, onReceive: (Intent) -> Unit): BroadcastReceiver {
    val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceive(intent)
        }
    }
    actions.forEach {
        registerReceiver(receiver, IntentFilter(it))
    }
    return receiver
}

fun Context.broadcastReceiverAsFlow(vararg actions: String, oneShot: Boolean = false) = callbackFlow {
    var receiver: BroadcastReceiver? = null
    receiver = registerReceiver(*actions){ intent ->
        trySend(intent)
        if(oneShot){
            receiver?.let {
                unregisterReceiver(it)
                receiver = null
            }
        }
    }
    awaitClose {
        receiver?.let {
            unregisterReceiver(it)
            receiver = null
        }
    }
}
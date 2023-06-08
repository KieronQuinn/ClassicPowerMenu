package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.annotation.SuppressLint
import android.app.IServiceConnection
import android.content.Context
import android.content.ServiceConnection
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 *  Returns whether the current context has a dark configuration
 */
internal val Context.isDarkMode: Boolean
    get() {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

val Context.isLandscape: Boolean
    get() {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

fun Context.getSecureSettingAsStringFlow(key: String) = callbackFlow<String?> {
    val observer = object: ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            trySend(Settings.Secure.getString(contentResolver, key))
        }
    }
    trySend(Settings.Secure.getString(contentResolver, key))
    contentResolver.registerContentObserver(Settings.Secure.getUriFor(key), false, observer)
    awaitClose {
        contentResolver.unregisterContentObserver(observer)
    }
}

// From https://stackoverflow.com/a/55280832
@SuppressLint("ResourceAsColor")
@ColorInt
fun Context.getColorResCompat(@AttrRes id: Int): Int {
    val resolvedAttr = TypedValue()
    this.theme.resolveAttribute(id, resolvedAttr, true)
    val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
    return ContextCompat.getColor(this, colorRes)
}

@SuppressLint("UnsafeOptInUsageError")
fun Context.getServiceDispatcher(serviceConnection: ServiceConnection, handler: Handler, flags: Int): IServiceConnection {
    return if(isAtLeastU()){
        Context::class.java.getMethod("getServiceDispatcher", ServiceConnection::class.java, Handler::class.java, Long::class.java)
            .invoke(this, serviceConnection, handler, flags.toLong()) as IServiceConnection
    }else{
        Context::class.java.getMethod("getServiceDispatcher", ServiceConnection::class.java, Handler::class.java, Integer.TYPE)
            .invoke(this, serviceConnection, handler, flags) as IServiceConnection
    }
}
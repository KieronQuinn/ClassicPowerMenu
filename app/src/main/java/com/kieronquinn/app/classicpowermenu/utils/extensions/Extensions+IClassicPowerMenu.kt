package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.annotation.SuppressLint
import android.app.IApplicationThread
import android.app.IServiceConnection
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.kieronquinn.app.classicpowermenu.IClassicPowerMenu
import com.kieronquinn.app.classicpowermenu.model.service.ActivityContainer
import com.kieronquinn.app.classicpowermenu.model.service.BroadcastContainer
import com.kieronquinn.app.classicpowermenu.model.service.ServiceBindContainer
import com.kieronquinn.app.classicpowermenu.model.service.ServiceUnbindContainer
import java.lang.Exception

private val boundServices = HashMap<Int, IServiceConnection>()

/**
 *  Bind a service of [Intent] [intent] to a [ServiceConnection] [serviceConnection]. This will be bound
 *  as root (`android`), bypassing permission checks. The [context] field must be a **Base** context,
 *  an Application context will **not** work.
 *
 *  Optional field [flags] to pass to the service dispatcher.
 */
@SuppressLint("PrivateApi")
fun IClassicPowerMenu.bindService(context: Context, intent: Intent, serviceConnection: ServiceConnection, flags: Int = Context.BIND_AUTO_CREATE or Context.BIND_WAIVE_PRIORITY, omitThread: Boolean = false): Int {
    val applicationThread = Context::class.java.getMethod("getIApplicationThread").invoke(context) as IApplicationThread
    val activityToken = Context::class.java.getMethod("getActivityToken").invoke(context) as? IBinder
    val mainThreadHandler = Context::class.java.getMethod("getMainThreadHandler").invoke(context) as Handler
    val serviceDispatcher = Context::class.java.getMethod("getServiceDispatcher", ServiceConnection::class.java, Handler::class.java, Integer.TYPE)
        .invoke(context, serviceConnection, mainThreadHandler, flags) as IServiceConnection
    val result = bindServicePrivileged(ServiceBindContainer(if(omitThread) null else applicationThread, activityToken, serviceDispatcher), intent, flags)
    boundServices[serviceConnection.hashCode()] = serviceDispatcher
    return result
}

/**
 *  Unbinds a [ServiceConnection] [serviceConnection] as root. The [context] field must be a **Base** context,
 *  an Application or Activity context will **not** work.
 */
@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
fun IClassicPowerMenu.unbindService(serviceConnection: ServiceConnection): Boolean {
    val iServiceConnection = boundServices[serviceConnection.hashCode()]
    return if(iServiceConnection != null) {
        unBindServicePrivileged(ServiceUnbindContainer(iServiceConnection))
        true
    }else false
}

/**
 *  Sends a broadcast of [intent] as root.
 */
@SuppressLint("PrivateApi")
fun IClassicPowerMenu.sendBroadcast(context: Context, intent: Intent) {
    val applicationThread = Context::class.java.getMethod("getIApplicationThread").invoke(context) as IApplicationThread
    val intentType = intent.resolveTypeIfNeeded(context.contentResolver)
    val attributionTag = Context::class.java.getMethod("getAttributionTag").invoke(context) as? String
    return sendBroadcastPrivileged(BroadcastContainer(applicationThread), intent, intentType, attributionTag)
}

/**
 *  Start an activity of [intent] as root.
 */
@SuppressLint("PrivateApi")
fun IClassicPowerMenu.startActivity(context: Context, intent: Intent, omitThread: Boolean = false): Int {
    val applicationThread = Context::class.java.getMethod("getIApplicationThread").invoke(context) as IApplicationThread
    return startActivityPrivileged(ActivityContainer(if(omitThread) null else applicationThread), intent)
}

fun IClassicPowerMenu.sendDismissIntent(context: Context) {
    val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    sendBroadcast(context, intent)
}
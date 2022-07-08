package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.IActivityManager
import android.app.IApplicationThread
import android.app.IServiceConnection
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.os.BuildCompat
import java.lang.Error
import java.lang.Exception
import kotlin.reflect.KFunction6

/**
 *  Handles differences between calls on Android 11 and 12+
 */
@SuppressLint("UnsafeOptInUsageError")
fun IActivityManager.broadcastIntentWithFeatureCompat(
    thread: IApplicationThread,
    attributionTag: String?,
    intent: Intent,
    intentType: String?,
    identifier: Int
) {
    val options = arrayOf(
        IActivityManager::broadcastIntentOptionA,
        IActivityManager::broadcastIntentOptionB,
        IActivityManager::broadcastIntentOptionC
    )
    val result = options.firstOrNull {
        it.invoke(this, thread, attributionTag, intent, intentType, identifier) == null
    }
    if(result == null){
        throw NoSuchMethodError("Failed to find broadcastIntentWithFeature method")
    }
}

private fun IActivityManager.broadcastIntentOptionA(
    thread: IApplicationThread,
    attributionTag: String?,
    intent: Intent,
    intentType: String?,
    identifier: Int
): Throwable? {
    return try {
        broadcastIntentWithFeature(
            thread,
            attributionTag,
            intent,
            intentType,
            null,
            Activity.RESULT_OK,
            null,
            null,
            null,
            null,
            null,
            -1,
            null,
            false,
            false,
            identifier
        )
        null
    }catch (e: NoSuchMethodError){
        e
    }
}

private fun IActivityManager.broadcastIntentOptionB(
    thread: IApplicationThread,
    attributionTag: String?,
    intent: Intent,
    intentType: String?,
    identifier: Int
): Throwable? {
    return try {
        broadcastIntentWithFeature(
            thread,
            attributionTag,
            intent,
            intentType,
            null,
            Activity.RESULT_OK,
            null,
            null,
            null,
            null,
            -1,
            null,
            false,
            false,
            identifier
        )
        null
    }catch (e: NoSuchMethodError){
        e
    }
}

private fun IActivityManager.broadcastIntentOptionC(
    thread: IApplicationThread,
    attributionTag: String?,
    intent: Intent,
    intentType: String?,
    identifier: Int
): Throwable? {
    return try {
        broadcastIntentWithFeature(
            thread,
            attributionTag,
            intent,
            intentType,
            null,
            Activity.RESULT_OK,
            null,
            null,
            null,
            -1,
            null,
            false,
            false,
            identifier
        )
        null
    }catch (e: NoSuchMethodError){
        e
    }
}

@SuppressLint("UnsafeOptInUsageError")
fun IActivityManager.bindServiceInstanceCompat(
    caller: IApplicationThread?,
    token: IBinder?,
    service: Intent?,
    resolvedType: String?,
    connection: IServiceConnection?,
    flags: Int,
    instanceName: String?,
    callingPackage: String?,
    userId: Int
): Int {
    return if (BuildCompat.isAtLeastT()) {
        bindServiceInstance(
            caller,
            token,
            service,
            resolvedType,
            connection,
            flags,
            instanceName,
            callingPackage,
            userId
        )
    } else {
        bindIsolatedService(
            caller,
            token,
            service,
            resolvedType,
            connection,
            flags,
            instanceName,
            callingPackage,
            userId
        )
    }
}
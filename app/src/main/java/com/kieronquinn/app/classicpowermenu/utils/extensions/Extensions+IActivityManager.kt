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
    when {
        BuildCompat.isAtLeastT() -> {
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
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
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
        }
        else -> {
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
        }
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
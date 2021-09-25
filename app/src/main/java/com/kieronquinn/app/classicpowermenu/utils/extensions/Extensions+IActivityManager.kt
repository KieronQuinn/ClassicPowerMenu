package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.app.Activity
import android.app.IActivityManager
import android.app.IApplicationThread
import android.content.Intent
import android.os.Build

/**
 *  Handles differences between calls on Android 11 and 12+
 */
fun IActivityManager.broadcastIntentWithFeatureCompat(
    thread: IApplicationThread,
    attributionTag: String?,
    intent: Intent,
    intentType: String?,
    identifier: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
    }else{
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
package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.app.Activity
import android.app.ActivityThread

/**
 *  Very hacky method to get the resumed activity via ActivityThread
 */
fun getTopActivity(): Activity? {
    val activityThread = ActivityThread.currentActivityThread()
    val activities = ActivityThread::class.java.getDeclaredField("mActivities").apply {
        isAccessible = true
    }.get(activityThread) as? Map<Any, Any> ?: return null
    for (activityRecord in activities.values) {
        val activityRecordClass: Class<*> = activityRecord.javaClass
        val isPaused = activityRecordClass.getDeclaredField("paused").apply {
            isAccessible = true
        }.getBoolean(activityRecord)
        if (!isPaused) {
            return activityRecordClass.getDeclaredField("activity").apply {
                isAccessible = true
            }.get(activityRecord) as? Activity
        }
    }
    return null
}
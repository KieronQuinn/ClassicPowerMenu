package com.android.systemui.util.extensions

import android.app.ActivityManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private object ActivityManagerServiceWrapper: KoinComponent {

    private val service by inject<ServiceRunner>()

    fun getCurrentUser(): Int {
        return service.getCurrentUser()
    }

}

fun ActivityManager_getCurrentUser(): Int {
    return ActivityManagerServiceWrapper.getCurrentUser()
}

fun ActivityManager.getUidImportance(uid: Int): Int {
    return ActivityManager::class.java.getMethod("getUidImportance", Integer.TYPE).invoke(this) as Int
}
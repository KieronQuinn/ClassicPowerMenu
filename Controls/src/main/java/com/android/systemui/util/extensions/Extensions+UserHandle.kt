package com.android.systemui.util.extensions

import android.os.UserHandle

fun UserHandle_of(userId: Int): UserHandle {
    return UserHandle::class.java.getMethod("of", Integer.TYPE).invoke(null, userId) as UserHandle
}

val UserHandle.identifier: Int
    get() {
        return this::class.java.getMethod("getIdentifier").invoke(this) as Int
    }

val UserHandle_USER_ALL = -1
val UserHandle_ALL
    get() = UserHandle_of(UserHandle_USER_ALL)

val UserHandle_USER_NULL = -10000
val UserHandle_NULL
    get() = UserHandle_of(UserHandle_USER_NULL)

val UserHandle_USER_CURRENT = -2
val UserHandle_CURRENT
    get() = UserHandle_of(UserHandle_USER_CURRENT)

val UserHandle_USER_SYSTEM = -2
val UserHandle_SYSTEM
    get() = UserHandle_of(UserHandle_USER_SYSTEM)
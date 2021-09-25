package com.android.systemui.util.extensions

import android.content.Context
import android.os.UserHandle

val Context.userId: Int
    get() {
        return this::class.java.getMethod("getUserId").invoke(this) as Int
    }

val Context.user: UserHandle
    get() {
        return this::class.java.getMethod("getUser").invoke(this) as UserHandle
    }
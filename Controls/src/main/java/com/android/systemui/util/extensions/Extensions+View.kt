package com.android.systemui.util.extensions

import android.view.View

val View.isVisibleToUser: Boolean
    get() {
        return View::class.java.getMethod("isVisibleToUser").invoke(this) as Boolean
    }
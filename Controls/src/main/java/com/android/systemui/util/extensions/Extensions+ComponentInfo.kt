package com.android.systemui.util.extensions

import android.content.ComponentName
import android.content.pm.ComponentInfo

val ComponentInfo.componentName
    get() = ComponentInfo::class.java.getMethod("getComponentName").invoke(this) as ComponentName
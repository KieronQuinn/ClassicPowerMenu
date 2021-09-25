package com.android.systemui.util.extensions

import android.service.controls.templates.ControlTemplate

val ControlTemplate_TYPE_NO_TEMPLATE = 0

val ControlTemplate_NO_TEMPLATE
    get() = ControlTemplate::class.java.getField("NO_TEMPLATE").get(null) as ControlTemplate
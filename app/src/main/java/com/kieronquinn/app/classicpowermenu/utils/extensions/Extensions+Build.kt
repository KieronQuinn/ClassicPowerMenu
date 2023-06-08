package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.os.Build

fun isAtLeastU(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

fun isAtLeastT(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}
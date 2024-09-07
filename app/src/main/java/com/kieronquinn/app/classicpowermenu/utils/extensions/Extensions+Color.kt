package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.graphics.Color

fun Int.toHexString(): String {
    return "#" + Integer.toHexString(this)
}


fun com.google.type.Color.toColor(): Int {
    return if(hasAlpha()){
        Color.valueOf(red, green, blue, alpha.value)
    }else{
        Color.valueOf(red, green, blue)
    }.toArgb()
}
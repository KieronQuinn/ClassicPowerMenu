package com.kieronquinn.app.classicpowermenu.utils.extensions

fun Int.toHexString(): String {
    return "#" + Integer.toHexString(this)
}
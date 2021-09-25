package com.android.systemui.util.extensions

/**
 *  Dagger bridge
 */
fun <T> Lazy<T>.get(): T {
    return value
}
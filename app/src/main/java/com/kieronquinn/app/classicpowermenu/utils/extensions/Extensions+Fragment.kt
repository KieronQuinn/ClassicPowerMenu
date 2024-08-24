package com.kieronquinn.app.classicpowermenu.utils.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope

/**
 *  Helper for [LifecycleOwner].[whenResumed]
 */
fun Fragment.whenResumed(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.whenResumed(block)
}

/**
 *  Helper for [LifecycleOwner].[whenCreated]
 */
fun Fragment.whenCreated(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.whenCreated(block)
}
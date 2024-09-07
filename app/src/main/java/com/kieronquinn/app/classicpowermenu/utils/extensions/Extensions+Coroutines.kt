package com.kieronquinn.app.classicpowermenu.utils.extensions

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.Continuation

suspend inline fun <T> suspendCoroutineWithTimeout(timeout: Long, crossinline block: (Continuation<T>) -> Unit ) : T? {
    var finalValue : T? = null
    withTimeoutOrNull(timeout) {
        finalValue = suspendCancellableCoroutine(block = block)
    }
    return finalValue
}

suspend inline fun <T> suspendCancellableCoroutineWithTimeout(timeout: Long, crossinline block: (CancellableContinuation<T>) -> Unit ) : T? {
    var finalValue : T? = null
    withTimeoutOrNull(timeout) {
        finalValue = suspendCancellableCoroutine(block = block)
    }
    return finalValue
}

fun <T> CoroutineScope.launch(lock: Mutex, block: suspend () -> T) {
    launch {
        lock.withLock {
            block()
        }
    }
}

package com.kieronquinn.app.classicpowermenu.utils.extensions

import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce

fun NavController.navigateSafely(directions: NavDirections){
    currentDestination?.getAction(directions.actionId)?.let { navigate(directions) }
}

fun NavController.navigateSafely(@IdRes action: Int){
    currentDestination?.getAction(action)?.let { navigate(action) }
}

fun Fragment.getTopFragment(): Fragment? {
    if(!isAdded) return null
    return childFragmentManager.fragments.firstOrNull()
}

fun NavController.onDestinationChanged() = callbackFlow {
    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        trySend(destination)
    }
    addOnDestinationChangedListener(listener)
    currentDestination?.let {
        trySend(it)
    }
    awaitClose {
        removeOnDestinationChangedListener(listener)
    }
}.debounce(250L)

fun NavController.setOnBackPressedCallback(callback: OnBackPressedCallback) {
    NavController::class.java.getDeclaredField("onBackPressedCallback").apply {
        isAccessible = true
    }.set(this, callback)
}
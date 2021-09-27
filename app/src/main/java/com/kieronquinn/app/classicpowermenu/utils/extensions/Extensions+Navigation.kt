package com.kieronquinn.app.classicpowermenu.utils.extensions

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.navigateSafely(directions: NavDirections){
    currentDestination?.getAction(directions.actionId)?.let { navigate(directions) }
}

fun NavController.navigateSafely(@IdRes action: Int){
    currentDestination?.getAction(action)?.let { navigate(action) }
}
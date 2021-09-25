package com.kieronquinn.app.classicpowermenu.components.navigation

import android.content.Intent
import android.util.Log
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface BaseNavigation {

    val navigationBus: Flow<NavigationEvent>

    suspend fun navigate(navDirections: NavDirections)
    suspend fun navigate(@IdRes id: Int)
    suspend fun navigate(intent: Intent)
    suspend fun navigateUpTo(@IdRes id: Int, popInclusive: Boolean = false)
    suspend fun navigateBack()

}

sealed class NavigationEvent {
    data class Directions(val directions: NavDirections): NavigationEvent()
    data class Id(@IdRes val id: Int): NavigationEvent()
    data class PopupTo(@IdRes val id: Int, val popInclusive: Boolean): NavigationEvent()
    data class Intent(val intent: android.content.Intent): NavigationEvent()
    object Back: NavigationEvent()
}

open class AppNavigationImpl: BaseNavigation, AppNavigation {

    private val _navigationBus = MutableSharedFlow<NavigationEvent>()
    override val navigationBus = _navigationBus.asSharedFlow()

    override suspend fun navigate(id: Int) {
        _navigationBus.emit(NavigationEvent.Id(id))
    }

    override suspend fun navigate(navDirections: NavDirections) {
        _navigationBus.emit(NavigationEvent.Directions(navDirections))
    }

    override suspend fun navigateBack() {
        _navigationBus.emit(NavigationEvent.Back)
    }

    override suspend fun navigateUpTo(id: Int, popInclusive: Boolean) {
        _navigationBus.emit(NavigationEvent.PopupTo(id, popInclusive))
    }

    override suspend fun navigate(intent: Intent) {
        _navigationBus.emit(NavigationEvent.Intent(intent))
    }

}

class PowerMenuNavigationImpl: AppNavigationImpl(), PowerMenuNavigation {

    private val _closePowerMenuBus = MutableSharedFlow<Unit>()
    override val closePowerMenuBus = _closePowerMenuBus.asSharedFlow()

    override suspend fun closePowerMenu() {
        _closePowerMenuBus.emit(Unit)
    }

}

//App and Power Menu have separate navigation that can be run simultaneously
interface AppNavigation: BaseNavigation

//Container has its own for top level changes
interface ContainerNavigation: AppNavigation
class ContainerNavigationImpl: AppNavigationImpl(), ContainerNavigation

/**
 *  PowerMenuNavigation has an extra event type for overriding all and just closing the menu
 *  (equivalent of using finish)
 */
interface PowerMenuNavigation: BaseNavigation {
    val closePowerMenuBus: Flow<Unit>

    suspend fun closePowerMenu()
}
package com.kieronquinn.app.classicpowermenu.ui.screens.settings.devicecontrols

import androidx.lifecycle.ViewModel
import com.kieronquinn.app.classicpowermenu.components.navigation.AppNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import kotlinx.coroutines.flow.Flow

abstract class SettingsDeviceControlsViewModel: ViewModel() {

    abstract val deviceControlsShow: Flow<Boolean>
    abstract val deviceControlsShowInitialState: Boolean
    abstract var allowWhileLocked: Boolean

    abstract fun onSwitchClicked()

}

class SettingsDeviceControlsViewModelImpl(private val settings: Settings, private val navigation: AppNavigation): SettingsDeviceControlsViewModel() {

    override val deviceControlsShow = settings.deviceControlsShowFlow
    override val deviceControlsShowInitialState by settings::deviceControlsShow
    override var allowWhileLocked by settings::deviceControlsAllowWhileLocked

    override fun onSwitchClicked() {
        settings.deviceControlsShow = !settings.deviceControlsShow
    }

}
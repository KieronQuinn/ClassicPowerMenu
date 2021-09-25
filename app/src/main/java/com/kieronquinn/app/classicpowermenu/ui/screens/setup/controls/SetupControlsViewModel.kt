package com.kieronquinn.app.classicpowermenu.ui.screens.setup.controls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class SetupControlsViewModel: ViewModel() {

    abstract val controlsEnabled: Boolean
    abstract val controlsEnabledFlow: Flow<Boolean>

    abstract fun onBackPressed()
    abstract fun onControlsSwitchClicked()
    abstract fun onNextClicked()
    
}

class SetupControlsViewModelImpl(private val settings: Settings, private val containerNavigation: ContainerNavigation): SetupControlsViewModel() {

    override val controlsEnabled = settings.deviceControlsShow
    override val controlsEnabledFlow = settings.deviceControlsShowFlow

    override fun onControlsSwitchClicked() {
        viewModelScope.launch {
            settings.deviceControlsShow = !settings.deviceControlsShow
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

    override fun onNextClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(SetupControlsFragmentDirections.actionSetupControlsFragmentToSetupCompleteFragment())
        }
    }

}
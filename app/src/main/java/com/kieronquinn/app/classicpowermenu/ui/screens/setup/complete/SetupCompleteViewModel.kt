package com.kieronquinn.app.classicpowermenu.ui.screens.setup.complete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.ui.screens.decision.DecisionFragmentDirections
import kotlinx.coroutines.launch

abstract class SetupCompleteViewModel: ViewModel() {

    abstract fun onBackPressed()
    abstract fun onFinishClicked()

}

class SetupCompleteViewModelImpl(private val settings: Settings, private val containerNavigation: ContainerNavigation): SetupCompleteViewModel() {

    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

    override fun onFinishClicked() {
        viewModelScope.launch {
            settings.hasSeenSetup = true
            containerNavigation.navigate(DecisionFragmentDirections.actionGlobalNavGraphSettingsContainer())
        }
    }

}
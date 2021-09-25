package com.kieronquinn.app.classicpowermenu.ui.screens.setup.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import kotlinx.coroutines.launch

abstract class SetupLandingViewModel: ViewModel() {

    abstract fun onGetStartedClicked()

}

class SetupLandingViewModelImpl(private val containerNavigation: ContainerNavigation): SetupLandingViewModel() {

    override fun onGetStartedClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(SetupLandingFragmentDirections.actionSetupLandingFragmentToSetupRootCheckFragment())
        }
    }

}
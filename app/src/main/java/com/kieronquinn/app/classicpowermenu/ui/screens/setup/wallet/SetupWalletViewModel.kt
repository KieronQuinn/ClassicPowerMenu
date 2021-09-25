package com.kieronquinn.app.classicpowermenu.ui.screens.setup.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class SetupWalletViewModel: ViewModel() {

    abstract val walletEnabled: Boolean
    abstract val walletEnabledFlow: Flow<Boolean>

    abstract fun onBackPressed()
    abstract fun onWalletSwitchClicked()
    abstract fun onNextClicked()

}

class SetupWalletViewModelImpl(private val settings: Settings, private val containerNavigation: ContainerNavigation): SetupWalletViewModel() {

    override val walletEnabled = settings.quickAccessWalletShow
    override val walletEnabledFlow = settings.quickAccessWalletShowFlow

    override fun onWalletSwitchClicked() {
        viewModelScope.launch {
            settings.quickAccessWalletShow = !settings.quickAccessWalletShow
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

    override fun onNextClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(SetupWalletFragmentDirections.actionSetupWalletFragmentToSetupControlsFragment())
        }
    }

}
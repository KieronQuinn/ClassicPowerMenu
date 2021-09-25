package com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.safemode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.PowerMenuNavigation
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import kotlinx.coroutines.launch

abstract class SafeModeTopSheetViewModel: ViewModel() {

    abstract fun onRebootSafeModeClicked()
    abstract fun onCancelClicked()

}

class SafeModeTopSheetViewModelImpl(private val service: CPMServiceContainer, private val navigation: PowerMenuNavigation): SafeModeTopSheetViewModel() {

    override fun onRebootSafeModeClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.reboot(true)
            }
            navigation.closePowerMenu()
        }
    }

    override fun onCancelClicked() {
        viewModelScope.launch {
            navigation.navigateBack()
        }
    }

}
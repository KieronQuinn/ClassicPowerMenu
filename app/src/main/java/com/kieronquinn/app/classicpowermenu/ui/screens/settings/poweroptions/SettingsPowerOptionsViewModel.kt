package com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.AppNavigation
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.container.SettingsContainerFragmentDirections
import kotlinx.coroutines.launch

abstract class SettingsPowerOptionsViewModel: ViewModel() {

    abstract var hideWhenLocked: Boolean
    abstract var openCollapsed: Boolean
    abstract var allowRotation: Boolean
    abstract var allowFullRotation: Boolean

    abstract fun onRearrangeClicked()

}

class SettingsPowerOptionsViewModelImpl(settings: Settings, private val containerNavigation: ContainerNavigation): SettingsPowerOptionsViewModel() {

    override var hideWhenLocked by settings::powerOptionsHideWhenLocked
    override var openCollapsed by settings::powerOptionsOpenCollapsed
    override var allowRotation by settings::allowRotation
    override var allowFullRotation by settings::allowFullRotation

    override fun onRearrangeClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(SettingsContainerFragmentDirections.actionSettingsContainerFragmentToSettingsPowerOptionsRearrangeFragment())
        }
    }

}


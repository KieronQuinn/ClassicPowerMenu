package com.kieronquinn.app.classicpowermenu.ui.screens.settings.developeroptions

import androidx.lifecycle.ViewModel
import com.kieronquinn.app.classicpowermenu.components.settings.Settings

abstract class SettingsDeveloperOptionsViewModel: ViewModel() {

    abstract var contentCreatorMode: Boolean

}

class SettingsDeveloperOptionsViewModelImpl(settings: Settings): SettingsDeveloperOptionsViewModel() {

    override var contentCreatorMode by settings::developerContentCreatorMode

}

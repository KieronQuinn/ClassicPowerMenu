package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.autoswitchservice

import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.autoswitch.AutoSwitchServicesRepository
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

abstract class SettingsQuickAccessWalletAutoSwitchServiceViewModel: ViewModel() {

    abstract val state: Flow<State>

    abstract fun onBackPressed()
    abstract fun onServiceClicked(service: AutoSwitchServiceItem)

    data class AutoSwitchServiceItem(val name: String, val componentName: String, val image: Drawable) {
        var selected = false
    }

    sealed class State {
        object Loading: State()
        data class Loaded(val services: ArrayList<AutoSwitchServiceItem>): State()
        data class Error(val type: ErrorType): State()
    }

    enum class ErrorType(@StringRes val contentRes: Int) {
        NO_SERVICES(R.string.settings_quick_access_wallet_auto_switch_service_error_no_cards)
    }

}

class SettingsQuickAccessWalletAutoSwitchServiceViewModelImpl(private val autoSwitchServicesRepository: AutoSwitchServicesRepository, private val settings: Settings, private val containerNavigation: ContainerNavigation): SettingsQuickAccessWalletAutoSwitchServiceViewModel() {

    private val autoSwitchServices = flow {
        val services = autoSwitchServicesRepository.getAutoSwitchServices()
        emit(services)
    }

    private val _state = autoSwitchServices.map { services ->
        when {
            services.isEmpty() -> {
                State.Error(ErrorType.NO_SERVICES)
            }
            else -> {
                State.Loaded(services)
            }
        }
    }

    override val state = _state.onStart { emit(State.Loading) }


    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

    override fun onServiceClicked(service: AutoSwitchServiceItem) {
        viewModelScope.launch {
            settings.quickAccessWalletSelectedAutoSwitchService = service.componentName
            containerNavigation.navigateBack()
        }
    }
}
package com.kieronquinn.app.classicpowermenu.ui.screens.settings.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jakewharton.processphoenix.ProcessPhoenix
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.components.navigation.AppNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.components.xposed.XposedSelfHook
import com.kieronquinn.app.classicpowermenu.service.accessibility.CPMAccessibilityService
import com.kieronquinn.app.classicpowermenu.utils.extensions.EXTRA_FRAGMENT_ARG_KEY
import com.kieronquinn.app.classicpowermenu.utils.extensions.EXTRA_SHOW_FRAGMENT_ARGUMENTS
import com.kieronquinn.app.classicpowermenu.utils.extensions.getSecureSettingAsStringFlow
import com.kieronquinn.app.classicpowermenu.utils.extensions.isAccessibilityServiceEnabled
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class SettingsMainViewModel: ViewModel() {

    abstract val enabled: Flow<Boolean>
    abstract val enabledInitialState: Boolean
    abstract var useMonet: Boolean
    abstract val developerOptionsEnabled: Flow<Boolean>
    abstract var accessibilityServiceDisabled: Boolean
    abstract val accessibilityServiceDisabledFlow: Flow<Boolean>

    abstract fun isDeveloperOptionsEnabled(): Boolean
    abstract fun isWallpaperColorPickerAvailable(): Boolean

    abstract fun onResume()
    abstract fun onAccessibilityWarningClicked()
    abstract fun onMainSwitchClicked()
    abstract fun onPowerOptionsClicked()
    abstract fun onQuickAccessWalletClicked()
    abstract fun onDeviceControlsClicked()
    abstract fun onWallpaperColorClicked()
    abstract fun onDeveloperOptionsClicked()
    abstract fun onFaqClicked()
    abstract fun onAboutTripleTapped()
    abstract fun onOpenSourceLibrariesClicked()
    abstract fun onSetupClicked(context: Context)

}

class SettingsMainViewModelImpl(context: Context, private val settings: Settings, private val navigation: AppNavigation): SettingsMainViewModel() {

    override val enabled = settings.enabledFlow
    override val enabledInitialState = settings.enabled
    override var useMonet by settings::useMonet
    override val developerOptionsEnabled = settings.developerOptionsEnabledFlow
    private val resumeBus = MutableSharedFlow<Unit>()

    override fun isWallpaperColorPickerAvailable() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

    override var accessibilityServiceDisabled = false
    private val isAccessibilityServiceDisabled = context.getSecureSettingAsStringFlow(android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    override val accessibilityServiceDisabledFlow = combine(isAccessibilityServiceDisabled, resumeBus){ _, _ ->
        if(XposedSelfHook().isXposedHooked()) return@combine false
        !isAccessibilityServiceEnabled(context, CPMAccessibilityService::class.java)
    }.apply {
        viewModelScope.launch {
            collect {
                accessibilityServiceDisabled = it
            }
        }
    }

    private val accessibilityIntent by lazy {
        Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val bundle = Bundle()
            val componentName = ComponentName(BuildConfig.APPLICATION_ID, CPMAccessibilityService::class.java.name).flattenToString()
            bundle.putString(EXTRA_FRAGMENT_ARG_KEY, componentName)
            putExtra(EXTRA_FRAGMENT_ARG_KEY, componentName)
            putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        }
    }

    private val openSourceLibrariesIntent by lazy {
        Intent(context, OssLicensesMenuActivity::class.java)
    }

    override fun onAccessibilityWarningClicked() {
        viewModelScope.launch {
            navigation.navigate(accessibilityIntent)
        }
    }

    override fun isDeveloperOptionsEnabled(): Boolean {
        return settings.developerOptionsEnabled
    }

    override fun onMainSwitchClicked() {
        viewModelScope.launch {
            settings.enabled = !settings.enabled
        }
    }

    override fun onPowerOptionsClicked() {
        viewModelScope.launch {
            navigation.navigate(SettingsMainFragmentDirections.actionSettingsMainFragmentToSettingsPowerOptionsFragment())
        }
    }

    override fun onQuickAccessWalletClicked() {
        viewModelScope.launch {
            navigation.navigate(SettingsMainFragmentDirections.actionSettingsMainFragmentToSettingsQuickAccessWalletFragment())
        }
    }

    override fun onDeviceControlsClicked() {
        viewModelScope.launch {
            navigation.navigate(SettingsMainFragmentDirections.actionSettingsMainFragmentToSettingsDeviceControlsFragment())
        }
    }

    override fun onWallpaperColorClicked() {
        viewModelScope.launch {
            navigation.navigate(SettingsMainFragmentDirections.actionSettingsMainFragmentToColorPickerBottomSheetFragment())
        }
    }

    override fun onDeveloperOptionsClicked() {
        viewModelScope.launch {
            navigation.navigate(SettingsMainFragmentDirections.actionSettingsMainFragmentToSettingsDeveloperOptionsFragment())
        }
    }

    override fun onFaqClicked() {
        viewModelScope.launch {
            navigation.navigate(SettingsMainFragmentDirections.actionSettingsMainFragmentToSettingsFaqFragment())
        }
    }

    override fun onAboutTripleTapped() {
        viewModelScope.launch {
            settings.developerOptionsEnabled = !settings.developerOptionsEnabled
        }
    }

    override fun onOpenSourceLibrariesClicked() {
        viewModelScope.launch {
            navigation.navigate(openSourceLibrariesIntent)
        }
    }

    override fun onSetupClicked(context: Context) {
        viewModelScope.launch {
            settings.hasSeenSetup = false
            ProcessPhoenix.triggerRebirth(context)
        }
    }

    override fun onResume() {
        viewModelScope.launch {
            resumeBus.emit(Unit)
        }
    }

}
package com.kieronquinn.app.classicpowermenu.ui.screens.setup.accessibility

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.xposed.XposedSelfHook
import com.kieronquinn.app.classicpowermenu.service.accessibility.CPMAccessibilityService
import com.kieronquinn.app.classicpowermenu.utils.extensions.EXTRA_FRAGMENT_ARG_KEY
import com.kieronquinn.app.classicpowermenu.utils.extensions.EXTRA_SHOW_FRAGMENT_ARGUMENTS
import com.kieronquinn.app.classicpowermenu.utils.extensions.getSecureSettingAsStringFlow
import com.kieronquinn.app.classicpowermenu.utils.extensions.isAccessibilityServiceEnabled
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class SetupAccessibilityViewModel: ViewModel() {

    abstract val accessibilityEnabled: Flow<Boolean>
    abstract fun onEnableClicked()
    abstract fun onSkipClicked()
    abstract fun onBackPressed()

}

class SetupAccessibilityViewModelImpl(context: Context, private val containerNavigation: ContainerNavigation): SetupAccessibilityViewModel() {

    override val accessibilityEnabled = context.getSecureSettingAsStringFlow(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).map {
        if(XposedSelfHook().isXposedHooked()) return@map true
        if(it.isNullOrBlank()) return@map false
        isAccessibilityServiceEnabled(context, CPMAccessibilityService::class.java, it)
    }

    init {
        viewModelScope.launch {
            accessibilityEnabled.collect {
                if(it){
                    moveToNext()
                }
            }
        }
    }

    private val accessibilityIntent by lazy {
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val bundle = Bundle()
            val componentName = ComponentName(BuildConfig.APPLICATION_ID, CPMAccessibilityService::class.java.name).flattenToString()
            bundle.putString(EXTRA_FRAGMENT_ARG_KEY, componentName)
            putExtra(EXTRA_FRAGMENT_ARG_KEY, componentName)
            putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        }
    }

    override fun onEnableClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(accessibilityIntent)
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

    override fun onSkipClicked() {
        viewModelScope.launch {
            moveToNext()
        }
    }

    private suspend fun moveToNext(){
        containerNavigation.navigate(SetupAccessibilityFragmentDirections.actionSetupAccessibilityFragmentToSetupWalletFragment())
    }

}
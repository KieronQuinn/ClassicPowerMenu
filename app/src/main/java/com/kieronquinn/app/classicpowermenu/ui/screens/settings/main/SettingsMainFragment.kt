package com.kieronquinn.app.classicpowermenu.ui.screens.settings.main

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.model.settings.SettingsItem
import com.kieronquinn.app.classicpowermenu.ui.base.AutoExpandOnRotate
import com.kieronquinn.app.classicpowermenu.ui.base.ProvidesOverflow
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.switched.SettingsSwitchedFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsMainFragment : SettingsSwitchedFragment(), AutoExpandOnRotate, ProvidesOverflow {

    private val viewModel by viewModel<SettingsMainViewModel>()

    override val onSwitchClicked by lazy {
        return@lazy viewModel::onMainSwitchClicked
    }

    override val switchChecked by lazy {
        viewModel.enabled
    }

    override val switchStartState by lazy {
        viewModel.enabledInitialState
    }

    override val switchText by lazy {
        getString(R.string.settings_main_switch)
    }

    override val items by lazy {
        listOf(
            SettingsItem.Warning(
                R.drawable.ic_warning,
                getString(R.string.settings_accessibility_warning),
                getText(R.string.settings_accessibility_warning_desc),
                visible = { viewModel.accessibilityServiceDisabled },
                tapAction = viewModel::onAccessibilityWarningClicked
            ),
            SettingsItem.Header(getString(R.string.settings_header_configuration)),
            SettingsItem.Setting(
                R.drawable.ic_settings_power_options,
                getString(R.string.settings_power_options),
                getText(R.string.settings_power_options_desc),
                tapAction = viewModel::onPowerOptionsClicked
            ),
            SettingsItem.Setting(
                R.drawable.ic_settings_quick_access_wallet,
                getString(R.string.settings_quick_access_wallet),
                getText(R.string.settings_quick_access_wallet_desc),
                tapAction = viewModel::onQuickAccessWalletClicked
            ),
            SettingsItem.Setting(
                R.drawable.ic_settings_controls,
                getString(R.string.settings_controls),
                getText(R.string.settings_controls_desc),
                tapAction = viewModel::onDeviceControlsClicked
            ),
            SettingsItem.Setting(
                R.drawable.ic_settings_developer_options,
                getString(R.string.settings_developer_options),
                visible = viewModel::isDeveloperOptionsEnabled,
                tapAction = viewModel::onDeveloperOptionsClicked
            ),
            SettingsItem.Header(getString(R.string.settings_theme_configuration)),
            SettingsItem.SwitchSetting(
                R.drawable.ic_settings_monet,
                getString(R.string.settings_monet),
                getText(R.string.settings_monet_desc),
                viewModel::useMonet
            ),
            SettingsItem.Setting(
                R.drawable.ic_settings_monet_color_picker,
                getString(R.string.settings_monet_color_picker),
                getText(R.string.settings_monet_color_picker_desc),
                visible = viewModel::isWallpaperColorPickerAvailable,
                tapAction = viewModel::onWallpaperColorClicked
            ),
            SettingsItem.Header(
                getString(R.string.settings_about_header)
            ),
            SettingsItem.Setting(
                R.drawable.ic_faq,
                getString(R.string.settings_about_faq),
                tapAction = viewModel::onFaqClicked
            ),
            SettingsItem.AboutSetting(
                R.drawable.ic_about,
                getString(R.string.settings_about, BuildConfig.VERSION_NAME),
                getText(R.string.settings_about_desc),
                tripleTapAction = viewModel::onAboutTripleTapped
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDeveloperOptions()
        setupAccessibilityWarning()
    }

    private fun setupDeveloperOptions() = lifecycleScope.launchWhenResumed {
        viewModel.developerOptionsEnabled.collect {
            recyclerView().adapter?.notifyDataSetChanged()
        }
    }

    private fun setupAccessibilityWarning() = lifecycleScope.launchWhenResumed {
        viewModel.accessibilityServiceDisabledFlow.collect {
            recyclerView().adapter?.notifyDataSetChanged()
        }
    }

    override fun inflateMenu(menuInflater: MenuInflater, menu: Menu) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.menu_open_source_libraries -> viewModel.onOpenSourceLibrariesClicked()
            R.id.menu_rerun_setup -> viewModel.onSetupClicked(requireActivity())
        }
        return true
    }

}
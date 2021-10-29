package com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions

import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.model.settings.SettingsItem
import com.kieronquinn.app.classicpowermenu.ui.base.AutoExpandOnRotate
import com.kieronquinn.app.classicpowermenu.ui.base.BackAvailable
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.generic.SettingsGenericFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsPowerOptionsFragment : SettingsGenericFragment(), BackAvailable, AutoExpandOnRotate {

    private val viewModel by viewModel<SettingsPowerOptionsViewModel>()
    override val applyStretchOverscrollCompat = false

    override val items by lazy {
        listOf(
            SettingsItem.Setting(
                R.drawable.ic_power_options_rearrange,
                getString(R.string.settings_power_options_rearrange),
                getText(R.string.settings_power_options_rearrange_desc),
                tapAction = viewModel::onRearrangeClicked
            ),
            SettingsItem.SwitchSetting(
                R.drawable.ic_power_options_hide_when_locked,
                getString(R.string.settings_power_options_hide_when_locked),
                getText(R.string.settings_power_options_hide_when_locked_desc),
                viewModel::hideWhenLocked
            ),
            SettingsItem.SwitchSetting(
                R.drawable.ic_power_options_open_collapsed,
                getString(R.string.settings_power_options_open_collapsed),
                getText(R.string.settings_power_options_open_collapsed_desc),
                viewModel::openCollapsed
            ),
            SettingsItem.SwitchSetting(
                R.drawable.ic_power_options_allow_rotation,
                getString(R.string.settings_power_options_allow_rotate),
                getText(R.string.settings_power_options_allow_rotate_desc),
                viewModel::allowRotation,
                tapAction = this::refreshItems
            ),
            SettingsItem.SwitchSetting(
                R.drawable.ic_power_options_allow_full_rotation,
                getString(R.string.settings_power_options_allow_rotate_full),
                getText(R.string.settings_power_options_allow_rotate_full_desc),
                viewModel::allowFullRotation,
                enabled = { viewModel.allowRotation }
            )
        )
    }

    private fun refreshItems(isChecked: Boolean): Boolean {
        recyclerView.invoke().adapter?.notifyDataSetChanged()
        return true
    }

}
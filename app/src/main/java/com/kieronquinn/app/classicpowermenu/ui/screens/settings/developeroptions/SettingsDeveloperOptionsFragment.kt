package com.kieronquinn.app.classicpowermenu.ui.screens.settings.developeroptions

import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.model.settings.SettingsItem
import com.kieronquinn.app.classicpowermenu.ui.base.AutoExpandOnRotate
import com.kieronquinn.app.classicpowermenu.ui.base.BackAvailable
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.generic.SettingsGenericFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsDeveloperOptionsFragment : SettingsGenericFragment(), BackAvailable, AutoExpandOnRotate {

    private val viewModel by viewModel<SettingsDeveloperOptionsViewModel>()
    override val applyStretchOverscrollCompat = false

    override val items by lazy {
        listOf(
            SettingsItem.SwitchSetting(
                R.drawable.ic_quick_access_wallet_hide_card_number,
                getString(R.string.settings_developer_options_content_creator_mode),
                getText(R.string.settings_developer_options_content_creator_mode_desc),
                viewModel::contentCreatorMode
            )
        )
    }

}
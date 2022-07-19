package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.model.settings.SettingsItem
import com.kieronquinn.app.classicpowermenu.ui.base.AutoExpandOnRotate
import com.kieronquinn.app.classicpowermenu.ui.base.BackAvailable
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.switched.SettingsSwitchedFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsQuickAccessWalletFragment: SettingsSwitchedFragment(), BackAvailable, AutoExpandOnRotate {

    override val applyStretchOverscrollCompat = true

    private val viewModel by viewModel<SettingsQuickAccessWalletViewModel>()

    override val adapterEnabled by lazy {
        viewModel.showQuickAccessWallet
    }

    override val adapterEnabledInitialState by lazy {
        viewModel.showQuickAccessWalletInitialState
    }

    override val switchStartState by lazy {
        viewModel.showQuickAccessWalletInitialState
    }

    private val paymentCardsHeader by lazy {
        SettingsItem.Header(getString(R.string.settings_quick_access_wallet_header_payment))
    }

    private val loyaltyCardsHeader by lazy {
        SettingsItem.Header(getString(R.string.settings_quick_access_wallet_loyalty_cards))
    }

    override val switchText by lazy {
        getString(R.string.settings_quick_access_wallet_switch)
    }

    override val onSwitchClicked by lazy {
        return@lazy viewModel::onSwitchClicked
    }

    override val switchChecked by lazy {
        viewModel.showQuickAccessWallet
    }

    private val paymentCardsItems by lazy {
        listOf(
            SettingsItem.Setting(
                R.drawable.ic_google_wallet,
                getString(R.string.settings_quick_access_wallet_change_settings_in_pay),
                getString(R.string.settings_quick_access_wallet_change_settings_in_pay_desc),
                tapAction = viewModel::onChangeGooglePaySettingsClicked
            ),
            SettingsItem.SwitchSetting(
                R.drawable.ic_quick_access_wallet_hide_card_number,
                getString(R.string.settings_quick_access_wallet_hide_card_number_when_locked),
                getText(R.string.settings_quick_access_wallet_hide_card_number_when_locked_desc),
                viewModel::hideCardNumber
            )
        )
    }

    private val loyaltyCardsUnsupportedItems by lazy {
        listOf(
            SettingsItem.Setting(
                R.drawable.ic_google_wallet,
                getString(R.string.settings_quick_access_wallet_loyalty_cards_not_supported),
                getString(R.string.settings_quick_access_wallet_loyalty_cards_not_supported_desc),
                visible = viewModel::isGooglePayInstalled
            )
        )
    }

    private val loyaltyCardsSupportedItems by lazy {
        listOf(
            SettingsItem.SwitchSetting(
                R.drawable.ic_settings_quick_access_wallet,
                getString(R.string.settings_quick_access_wallet_show_loyalty_cards),
                getString(R.string.settings_quick_access_wallet_show_loyalty_cards_desc),
                viewModel::showLoyaltyCards
            ),
            SettingsItem.Setting(
                R.drawable.ic_quick_access_wallet_rearrange,
                getString(R.string.settings_quick_access_wallet_rearrange),
                getString(R.string.settings_quick_access_wallet_rearrange_desc),
                enabled = { viewModel.showLoyaltyCards },
                tapAction = viewModel::onReorderLoyaltyCardsClicked
            ),
            SettingsItem.SwitchSetting(
                R.drawable.ic_power_options_hide_when_locked,
                getString(R.string.settings_quick_access_wallet_access_while_locked),
                getString(R.string.settings_quick_access_wallet_access_while_locked_desc),
                viewModel::accessWhileLocked,
                enabled = { viewModel.showLoyaltyCards }
            ),
            SettingsItem.SwitchSetting(
                R.drawable.ic_quick_access_wallet_show_preview,
                getString(R.string.settings_quick_access_wallet_show_preview),
                getString(R.string.settings_quick_access_wallet_show_preview_desc),
                viewModel::showPreview,
                enabled = { viewModel.showLoyaltyCards }
            )
        )
    }

    override val items by lazy {
        listOf(paymentCardsHeader) + paymentCardsItems + listOf(loyaltyCardsHeader) + if(viewModel.isGooglePayInstalled) {
            loyaltyCardsSupportedItems
        }else{
            loyaltyCardsUnsupportedItems
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoyaltyCardsListener()
    }

    private fun setupLoyaltyCardsListener(){
        val recyclerView = recyclerView.invoke()
        lifecycleScope.launchWhenResumed {
            viewModel.showLoyaltyCardsChanged.collect {
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

}
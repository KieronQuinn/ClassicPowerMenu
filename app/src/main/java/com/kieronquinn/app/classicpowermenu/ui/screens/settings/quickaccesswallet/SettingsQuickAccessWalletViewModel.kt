package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.AppNavigation
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.GooglePayConstants
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.GoogleApiRepository
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.ValuablesDatabaseRepository
import com.kieronquinn.app.classicpowermenu.components.settings.EncryptedSettings
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.container.SettingsContainerFragmentDirections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class SettingsQuickAccessWalletViewModel: ViewModel() {

    abstract val showQuickAccessWallet: Flow<Boolean>
    abstract val showQuickAccessWalletInitialState: Boolean
    abstract var showLoyaltyCards: Boolean
    abstract var showPreview: Boolean
    abstract var accessWhileLocked: Boolean
    abstract var hideCardNumber: Boolean
    abstract var autoSwitchService: Boolean
    abstract val selectedAutoSwitchService: String
    abstract val showLoyaltyCardsChanged: Flow<Unit>
    abstract val autoSwitchServiceChanged: Flow<Unit>
    abstract val showLogoutOption: Boolean

    abstract val isGooglePayInstalled: Boolean

    abstract fun onSwitchClicked()
    abstract fun onChangeGooglePaySettingsClicked()
    abstract fun onReorderLoyaltyCardsClicked()
    abstract fun onAutoSwitchServiceClicked()
    abstract fun onLogoutClicked()
}

class SettingsQuickAccessWalletViewModelImpl(context: Context, private val settings: Settings, private val googleApiRepository: GoogleApiRepository, private val encryptedSettings: EncryptedSettings, private val valuablesDatabaseRepository: ValuablesDatabaseRepository, val navigation: AppNavigation, private val containerNavigation: ContainerNavigation): SettingsQuickAccessWalletViewModel() {

    private val packageManager = context.packageManager

    override val showQuickAccessWallet = settings.quickAccessWalletShowFlow
    override val showQuickAccessWalletInitialState = settings.quickAccessWalletShow
    override var showLoyaltyCards by settings::quickAccessWalletShowLoyaltyCards
    override var showPreview by settings::quickAccessWalletShowPreview
    override var accessWhileLocked by settings::quickAccessWalletAccessWhileLocked
    override var hideCardNumber by settings::quickAccessWalletHideCardNumberWhenLocked
    override var autoSwitchService by settings::quickAccessWalletAutoSwitchService
    override var selectedAutoSwitchService by settings::quickAccessWalletSelectedAutoSwitchService
    override val autoSwitchServiceChanged = settings.quickAccessWalletAutoSwitchServiceFlow.map{}
    override val showLoyaltyCardsChanged = settings.quickAccessWalletShowLoyaltyCardsFlow.map { }

    override val isGooglePayInstalled
        get() = GooglePayConstants.isGooglePayInstalled(packageManager) || GooglePayConstants.isGPayInstalled(packageManager)

    override val showLogoutOption
        get() = googleApiRepository.isSignedIn()


    override fun onSwitchClicked() {
        settings.quickAccessWalletShow = !settings.quickAccessWalletShow
    }

    override fun onChangeGooglePaySettingsClicked() {
        viewModelScope.launch {
            navigation.navigate(getGooglePayIntent() ?: return@launch)
        }
    }

    override fun onReorderLoyaltyCardsClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(SettingsContainerFragmentDirections.actionSettingsContainerFragmentToSettingsQuickAccessWalletManageFragment())
        }
    }

    override fun onAutoSwitchServiceClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(SettingsContainerFragmentDirections.actionSettingsContainerFragmentToSettingsQuickAccessWalletAutoSwitchServiceFragment())
        }
    }

    override fun onLogoutClicked() {
        encryptedSettings.walletToken = ""
        encryptedSettings.aasToken = ""
        viewModelScope.launch {
            valuablesDatabaseRepository.deleteAllRecords()
        }
    }

    private fun getGooglePayIntent(): Intent? {
        return if(GooglePayConstants.isGPayInstalled(packageManager)){
            packageManager.getLaunchIntentForPackage(GooglePayConstants.NBU_PAISA_PACKAGE_NAME)
        }else{
            packageManager.getLaunchIntentForPackage(GooglePayConstants.WALLET_NFC_REL_PACKAGE_NAME)
        }
    }

}
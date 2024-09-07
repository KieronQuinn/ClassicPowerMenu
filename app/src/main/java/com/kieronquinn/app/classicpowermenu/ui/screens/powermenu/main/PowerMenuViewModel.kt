package com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.main

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.systemui.plugin.globalactions.wallet.WalletCardViewInfo
import com.android.systemui.plugins.ActivityStarter
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.PowerMenuNavigation
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.GoogleWalletRepository
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButton
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButtonId
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.LoyaltyCard
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.utils.EmergencyDialerConstants
import com.kieronquinn.app.classicpowermenu.utils.extensions.*
import kotlinx.coroutines.launch

abstract class PowerMenuViewModel: ViewModel() {

    abstract suspend fun shouldShowLockdown(): Boolean

    abstract fun onPowerOffClicked()
    abstract fun onPowerOffLongClicked()

    abstract fun onRebootClicked()
    abstract fun onRebootLongClicked()
    abstract fun onRebootRecoveryClicked()
    abstract fun onRebootBootloaderClicked()
    abstract fun onRestartSystemUIClicked()
    abstract fun onRebootFastbootdClicked()
    abstract fun onRebootDownloadClicked()

    abstract fun onEmergencyClicked(context: Context)
    abstract fun onLockdownClicked()
    abstract fun onScreenshotClicked()

    abstract fun addLoyaltyCardsToWallet(list: ArrayList<WalletCardViewInfo>, callback: Runnable)

    abstract fun loadPowerMenuButtons(context: Context): List<PowerMenuButton>

    abstract val powerOptionsHideWhenLocked: Boolean
    abstract val powerOptionsOpenCollapsed: Boolean
    abstract val showQuickAccessWallet: Boolean
    abstract val showControls: Boolean
    abstract val monetEnabled: Boolean
    abstract val useSolidBackground: Boolean
    abstract val quickAccessWalletAutoSwitchService: Boolean
    abstract val quickAccessWalletSelectedAutoSwitchService: String
}

class PowerMenuViewModelImpl(context: Context, private val service: CPMServiceContainer, private val navigation: PowerMenuNavigation, private val settings: Settings, private val googleWalletRepository: GoogleWalletRepository, private val activityStarter: ActivityStarter): PowerMenuViewModel() {

    private val telecomManager by lazy {
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    }

    private val keyguardManager by lazy {
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }

    override val powerOptionsHideWhenLocked by settings::powerOptionsHideWhenLocked
    override val powerOptionsOpenCollapsed by settings::powerOptionsOpenCollapsed
    override val showControls by settings::deviceControlsShow
    override val showQuickAccessWallet by settings::quickAccessWalletShow
    override val monetEnabled by settings::useMonet
    override val useSolidBackground by settings::useSolidBackground
    override val quickAccessWalletAutoSwitchService by settings::quickAccessWalletAutoSwitchService
    override val quickAccessWalletSelectedAutoSwitchService by settings::quickAccessWalletSelectedAutoSwitchService

    override suspend fun shouldShowLockdown(): Boolean {
        //No point showing lockdown without a lock
        if(!keyguardManager.isDeviceSecure) return false
        return service.runWithService {
            val authState = it.strongAuth
            return@runWithService authState == LockPatternUtils_SOME_AUTH_REQUIRED_AFTER_USER_REQUEST || authState == LockPatternUtils_STRONG_AUTH_NOT_REQUIRED
        }
    }

    override fun onPowerOffClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.shutdown()
            }
            navigation.closePowerMenu()
        }
    }

    override fun onPowerOffLongClicked() {
        viewModelScope.launch {
            navigation.navigate(PowerMenuFragmentDirections.actionPowerMenuFragmentToSafeModeTopSheetFragment())
        }
    }

    override fun onRebootClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.reboot(false)
            }
            navigation.closePowerMenu()
        }
    }

    override fun onRebootRecoveryClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.rebootWithReason("recovery")
            }
            navigation.closePowerMenu()
        }
    }

    override fun onRebootBootloaderClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.rebootWithReason("bootloader")
            }
            navigation.closePowerMenu()
        }
    }

    override fun onRebootFastbootdClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.rebootWithReason("fastboot")
            }
            navigation.closePowerMenu()
        }
    }

    override fun onRebootDownloadClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.rebootWithReason("download")
            }
            navigation.closePowerMenu()
        }
    }

    override fun onRestartSystemUIClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.restartSystemUi()
            }
            navigation.closePowerMenu()
        }
    }

    override fun onRebootLongClicked() {
        viewModelScope.launch {
            navigation.navigate(PowerMenuFragmentDirections.actionPowerMenuFragmentToSafeModeTopSheetFragment())
        }
    }

    override fun onEmergencyClicked(context: Context) {
        val intent = telecomManager.createLaunchEmergencyDialerIntent(context, null)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(
            EmergencyDialerConstants.EXTRA_ENTRY_TYPE,
            EmergencyDialerConstants.ENTRY_TYPE_POWER_MENU
        )
        viewModelScope.launch {
            service.runWithService {
                it.startActivity(context, intent, omitThread = true)
            }
            navigation.closePowerMenu()
        }
    }

    override fun onLockdownClicked() {
        viewModelScope.launch {
            service.runWithService {
                it.lockdown()
            }
            navigation.closePowerMenu()
        }
    }

    override fun onScreenshotClicked() {
        viewModelScope.launch {
            navigation.closePowerMenu()
            service.runWithService {
                it.takeScreenshot()
            }
        }
    }

    override fun addLoyaltyCardsToWallet(list: ArrayList<WalletCardViewInfo>, callback: Runnable) {
        viewModelScope.launch {
            val loyaltyCards = googleWalletRepository.getLoyaltyCards(this@PowerMenuViewModelImpl::onCardClicked)?.run {
                val hidden = settings.quickAccessWalletLoyaltyCardsHidden
                val order = settings.quickAccessWalletLoyaltyCardsOrder
                filterNot { hidden.contains(it.getLoyaltyIdOrNull()) }.sortedBy {
                    val loyaltyId = it.getLoyaltyIdOrNull() ?: return@sortedBy 0
                    order.indexOf(loyaltyId)
                }
            }
            list.addAll(loyaltyCards ?: emptyList())
            callback.run()
        }
    }

    private fun onCardClicked(loyaltyCard: LoyaltyCard): Boolean {
        val launchPreview = {
            viewModelScope.launch {
                navigation.navigate(PowerMenuFragmentDirections.actionPowerMenuFragmentToWalletCodeDialogFragment(loyaltyCard))
            }
        }
        //Launch Google Pay when unlocked or if the user wants to
        if(!isLocked() && !settings.quickAccessWalletShowPreview) return false
        //Check if the user has disabled show while locked
        if(!settings.quickAccessWalletAccessWhileLocked && isLocked()){
            //Check if the user has opted to show a preview rather than Google Pay
            if(settings.quickAccessWalletShowPreview) {
                activityStarter.dismissKeyguardThenExecute({
                    launchPreview()
                    true
                }, null, true)
            }else{
                //Let the controller handle it
                return false
            }
        }else{
            launchPreview()
        }
        return true
    }

    override fun loadPowerMenuButtons(context: Context): List<PowerMenuButton> {
        return settings.powerMenuButtons.map {
            loadPowerMenuButton(context, it)
        }
    }

    private fun loadPowerMenuButton(context: Context, buttonId: PowerMenuButtonId) = when(buttonId){
        PowerMenuButtonId.EMERGENCY -> PowerMenuButton.Emergency(onClick = { onEmergencyClicked(context) })
        PowerMenuButtonId.REBOOT -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT,
            R.drawable.ic_reboot,
            context.getString(R.string.power_menu_button_reboot),
            ::onRebootClicked,
            ::onRebootLongClicked,
            shouldShow = ::shouldShowPowerOption
        )
        PowerMenuButtonId.POWER_OFF -> PowerMenuButton.Button(
            PowerMenuButtonId.POWER_OFF,
            R.drawable.ic_power_off,
            context.getString(R.string.power_menu_button_power_off),
            ::onPowerOffClicked,
            ::onPowerOffLongClicked,
            shouldShow = ::shouldShowPowerOption
        )
        PowerMenuButtonId.LOCKDOWN -> PowerMenuButton.Button(
            PowerMenuButtonId.LOCKDOWN,
            R.drawable.ic_lockdown,
            context.getString(R.string.power_menu_button_lockdown),
            ::onLockdownClicked,
            shouldShow = ::shouldShowLockdown
        )
        PowerMenuButtonId.SCREENSHOT -> PowerMenuButton.Button(
            PowerMenuButtonId.SCREENSHOT,
            R.drawable.ic_screenshot,
            context.getString(R.string.power_menu_button_screenshot),
            ::onScreenshotClicked
        )
        PowerMenuButtonId.REBOOT_RECOVERY -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_RECOVERY,
            R.drawable.ic_reboot_recovery,
            context.getString(R.string.power_menu_button_reboot_recovery),
            ::onRebootRecoveryClicked,
            shouldShow = ::shouldShowPowerOption
        )
        PowerMenuButtonId.REBOOT_BOOTLOADER -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_BOOTLOADER,
            R.drawable.ic_reboot_bootloader,
            context.getString(R.string.power_menu_button_reboot_bootloader),
            ::onRebootBootloaderClicked,
            shouldShow = ::shouldShowPowerOption
        )
        PowerMenuButtonId.RESTART_SYSTEMUI -> PowerMenuButton.Button(
            PowerMenuButtonId.RESTART_SYSTEMUI,
            R.drawable.ic_restart_systemui,
            context.getString(R.string.power_menu_button_restart_systemui),
            ::onRestartSystemUIClicked,
            shouldShow = ::shouldShowPowerOption
        )
        PowerMenuButtonId.REBOOT_FASTBOOTD -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_FASTBOOTD,
            R.drawable.ic_reboot_fastbootd,
            context.getString(R.string.power_menu_button_fastbootd),
            ::onRebootFastbootdClicked,
            shouldShow = ::shouldShowPowerOption
        )
        PowerMenuButtonId.REBOOT_DOWNLOAD -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_DOWNLOAD,
            R.drawable.ic_reboot_download,
            context.getString(R.string.power_menu_button_download),
            ::onRebootDownloadClicked,
            shouldShow = ::shouldShowPowerOption
        )
    }

    private fun shouldShowPowerOption(): Boolean {
        return !isLocked() || !settings.powerOptionsHideWhenLocked
    }

    private fun isLocked(): Boolean {
        return keyguardManager.isDeviceLocked
    }

}
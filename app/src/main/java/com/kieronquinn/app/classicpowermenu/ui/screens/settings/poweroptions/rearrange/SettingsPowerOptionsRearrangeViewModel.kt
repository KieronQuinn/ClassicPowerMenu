package com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions.rearrange

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButton
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButtonId
import kotlinx.coroutines.launch

abstract class SettingsPowerOptionsRearrangeViewModel: ViewModel() {

    abstract val monetEnabled: Boolean

    abstract fun loadPowerMenuButtons(context: Context): List<PowerMenuButton>
    abstract fun loadUnusedPowerMenuButtons(context: Context): List<PowerMenuButton>

    abstract fun savePowerMenuButtons(list: List<PowerMenuButtonId>)
    abstract fun onBackPressed()

}

class SettingsPowerOptionsRearrangeViewModelImpl(private val settings: Settings, private val containerNavigation: ContainerNavigation): SettingsPowerOptionsRearrangeViewModel() {

    override val monetEnabled by settings::useMonet

    override fun loadPowerMenuButtons(context: Context): List<PowerMenuButton> {
        return settings.powerMenuButtons.map { loadPowerMenuButton(context, it) }
    }

    override fun loadUnusedPowerMenuButtons(context: Context): List<PowerMenuButton> {
        val usedButtons = settings.powerMenuButtons
        return PowerMenuButtonId.values().filterNot { usedButtons.contains(it) }.map { loadPowerMenuButton(context, it) }
    }

    override fun savePowerMenuButtons(list: List<PowerMenuButtonId>) {
        settings.powerMenuButtons = list
    }

    private fun loadPowerMenuButton(context: Context, buttonId: PowerMenuButtonId) = when(buttonId) {
        PowerMenuButtonId.EMERGENCY -> PowerMenuButton.Emergency(
            this::emptyClick
        )
        PowerMenuButtonId.REBOOT -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT,
            R.drawable.ic_reboot,
            context.getString(R.string.power_menu_button_reboot),
            this::emptyClick
        )
        PowerMenuButtonId.POWER_OFF -> PowerMenuButton.Button(
            PowerMenuButtonId.POWER_OFF,
            R.drawable.ic_power_off,
            context.getString(R.string.power_menu_button_power_off),
            this::emptyClick
        )
        PowerMenuButtonId.LOCKDOWN -> PowerMenuButton.Button(
            PowerMenuButtonId.LOCKDOWN,
            R.drawable.ic_lockdown,
            context.getString(R.string.power_menu_button_lockdown),
            this::emptyClick
        )
        PowerMenuButtonId.SCREENSHOT -> PowerMenuButton.Button(
            PowerMenuButtonId.SCREENSHOT,
            R.drawable.ic_screenshot,
            context.getString(R.string.power_menu_button_screenshot),
            this::emptyClick
        )
        PowerMenuButtonId.REBOOT_RECOVERY -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_RECOVERY,
            R.drawable.ic_reboot_recovery,
            context.getString(R.string.power_menu_button_reboot_recovery),
            this::emptyClick
        )
        PowerMenuButtonId.REBOOT_BOOTLOADER -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_BOOTLOADER,
            R.drawable.ic_reboot_bootloader,
            context.getString(R.string.power_menu_button_reboot_bootloader),
            this::emptyClick
        )
        PowerMenuButtonId.RESTART_SYSTEMUI -> PowerMenuButton.Button(
            PowerMenuButtonId.RESTART_SYSTEMUI,
            R.drawable.ic_restart_systemui,
            context.getString(R.string.power_menu_button_restart_systemui),
            this::emptyClick
        )
        PowerMenuButtonId.REBOOT_FASTBOOTD -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_FASTBOOTD,
            R.drawable.ic_reboot_fastbootd,
            context.getString(R.string.power_menu_button_fastbootd),
            this::emptyClick
        )
        PowerMenuButtonId.REBOOT_DOWNLOAD -> PowerMenuButton.Button(
            PowerMenuButtonId.REBOOT_DOWNLOAD,
            R.drawable.ic_reboot_download,
            context.getString(R.string.power_menu_button_download),
            this::emptyClick
        )
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

    private fun emptyClick() = Unit

}
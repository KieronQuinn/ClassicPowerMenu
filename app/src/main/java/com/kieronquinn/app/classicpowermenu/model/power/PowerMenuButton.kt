package com.kieronquinn.app.classicpowermenu.model.power

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

sealed class PowerMenuButton(
    open val type: PowerMenuButtonType,
    open val id: PowerMenuButtonId?,
    @DrawableRes open val icon: Int,
    open val text: CharSequence,
    open val onClick: () -> Unit,
    open val onLongClick: (() -> Unit)? = null,
    @ColorRes open val backgroundColor: Int? = null,
    open val shouldShow: suspend () -> Boolean = { true }
){

    //Emergency power button has its own layout with hardcoded icon & text, not dynamic
    data class Emergency(
        override val onClick: () -> Unit) : PowerMenuButton(
        PowerMenuButtonType.EMERGENCY,
        PowerMenuButtonId.EMERGENCY,
        0,
        "",
        onClick,
        null
    )

    data class Button(
        override val id: PowerMenuButtonId,
        @DrawableRes override val icon: Int,
        override val text: CharSequence,
        override val onClick: () -> Unit,
        override val onLongClick: (() -> Unit)? = null,
        @ColorRes override val backgroundColor: Int? = null,
        override val shouldShow: suspend () -> Boolean = { true }
    ) : PowerMenuButton(PowerMenuButtonType.BUTTON, id, icon, text, onClick, onLongClick, backgroundColor)

    data class Empty(var isHighlighted: Boolean): PowerMenuButton(PowerMenuButtonType.EMPTY, null, 0, "", {})

}

enum class PowerMenuButtonType {
    EMERGENCY, BUTTON, EMPTY
}

enum class PowerMenuButtonId {
    EMERGENCY, POWER_OFF, REBOOT, LOCKDOWN, SCREENSHOT, REBOOT_RECOVERY, REBOOT_BOOTLOADER, RESTART_SYSTEMUI;

    companion object {
        val DEFAULT = listOf(EMERGENCY, POWER_OFF, REBOOT, LOCKDOWN, SCREENSHOT)
    }
}
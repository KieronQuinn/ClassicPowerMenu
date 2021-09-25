package com.kieronquinn.app.classicpowermenu.model.settings

import androidx.annotation.DrawableRes
import kotlin.reflect.KMutableProperty0

sealed class SettingsItem constructor(open val itemType: SettingsItemType, open var centerIconVertically: Boolean = true, open var visible: () -> Boolean = {true}, open var enabled: () -> Boolean = {true}) {
    data class Header(val title: String, override var visible: () -> Boolean = {true}): SettingsItem(SettingsItemType.HEADER)
    data class Setting(
        @DrawableRes val icon: Int,
        val title: String,
        var content: CharSequence? = null,
        override var centerIconVertically: Boolean = true,
        override var visible: () -> Boolean = {true},
        override var enabled: () -> Boolean = {true},
        val tapAction: (() -> Unit)? = null
    ): SettingsItem(SettingsItemType.SETTING)
    data class Warning(
        @DrawableRes val icon: Int,
        val title: String,
        var content: CharSequence? = null,
        override var centerIconVertically: Boolean = true,
        override var visible: () -> Boolean = {true},
        override var enabled: () -> Boolean = {true},
        val tapAction: (() -> Unit)? = null
    ): SettingsItem(SettingsItemType.WARNING)
    data class SwitchSetting(
        @DrawableRes val icon: Int,
        val title: String,
        var content: CharSequence? = null,
        val setting: KMutableProperty0<Boolean>,
        override var centerIconVertically: Boolean = true,
        override var visible: () -> Boolean = {true},
        override var enabled: () -> Boolean = {true},
        val tapAction: ((Boolean) -> Boolean)? = null
    ): SettingsItem(SettingsItemType.SWITCH_SETTING)
    data class AboutSetting(
        @DrawableRes val icon: Int,
        val title: String,
        var content: CharSequence? = null,
        override var visible: () -> Boolean = {true},
        val tripleTapAction: (() -> Unit)? = null
    ): SettingsItem(SettingsItemType.ABOUT_SETTING)
}

enum class SettingsItemType {
    HEADER, SETTING, WARNING, SWITCH_SETTING, ABOUT_SETTING
}
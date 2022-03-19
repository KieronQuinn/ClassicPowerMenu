package com.kieronquinn.app.classicpowermenu.components.settings

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButtonId
import com.kieronquinn.app.classicpowermenu.utils.extensions.getPreferenceAsFlow
import com.kieronquinn.app.classicpowermenu.utils.extensions.toHexString
import kotlinx.coroutines.flow.Flow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Settings {

    abstract var hasSeenSetup: Boolean
    abstract var enabled: Boolean
    abstract val enabledFlow: Flow<Boolean>
    abstract var useMonet: Boolean
    abstract var monetColor: Int?
    abstract var useSolidBackground: Boolean
    abstract var powerOptionsHideWhenLocked: Boolean
    abstract var powerOptionsOpenCollapsed: Boolean
    abstract var allowRotation: Boolean
    abstract var allowFullRotation: Boolean
    abstract var powerMenuButtons: List<PowerMenuButtonId>
    abstract var quickAccessWalletShow: Boolean
    abstract val quickAccessWalletShowFlow: Flow<Boolean>
    abstract var quickAccessWalletShowLoyaltyCards: Boolean
    abstract val quickAccessWalletShowLoyaltyCardsFlow: Flow<Boolean>
    abstract var quickAccessWalletShowPreview: Boolean
    abstract var quickAccessWalletAccessWhileLocked: Boolean
    abstract var quickAccessWalletHideCardNumberWhenLocked: Boolean
    abstract var quickAccessWalletLoyaltyCardsOrder: List<String>
    abstract var quickAccessWalletLoyaltyCardsHidden: List<String>
    abstract val quickAccessWalletLoyaltyCardsHiddenFlow: Flow<List<String>>
    abstract var deviceControlsShow: Boolean
    abstract val deviceControlsShowFlow: Flow<Boolean>
    abstract var deviceControlsAllowWhileLocked: Boolean
    abstract var developerOptionsEnabled: Boolean
    abstract val developerOptionsEnabledFlow: Flow<Boolean>
    abstract var developerContentCreatorMode: Boolean

}

class SettingsImpl(context: Context): Settings() {

    companion object {

        private const val KEY_HAS_SEEN_SETUP = "has_seen_setup"
        private const val DEFAULT_HAS_SEEN_SETUP = false

        private const val KEY_ENABLED = "enabled"
        private const val DEFAULT_ENABLED = true

        private const val KEY_USE_MONET = "use_monet"
        private const val DEFAULT_USE_MONET = true

        private const val KEY_MONET_COLOR = "monet_color"

        private const val KEY_USE_SOLID_BACKGROUND = "use_solid_background"
        private const val DEFAULT_USE_SOLID_BACKGROUND = false

        //Power Options
        private const val KEY_POWER_OPTIONS_HIDE_WHEN_LOCKED = "power_options_hide_when_locked"
        private const val DEFAULT_POWER_OPTIONS_HIDE_WHEN_LOCKED = false

        private const val KEY_POWER_OPTIONS_OPEN_COLLAPSED = "power_options_open_collapsed"
        private const val DEFAULT_POWER_OPTIONS_OPEN_COLLAPSED = false

        private const val KEY_POWER_MENU_BUTTONS = "power_options"
        private val DEFAULT_POWER_MENU_BUTTONS = PowerMenuButtonId.DEFAULT

        private const val KEY_ALLOW_ROTATION = "allow_rotation"
        private const val DEFAULT_ALLOW_ROTATION = true

        private const val KEY_ALLOW_FULL_ROTATION = "allow_full_rotation"
        private const val DEFAULT_ALLOW_FULL_ROTATION = false

        //Quick Access Wallet
        private const val KEY_QUICK_ACCESS_WALLET_SHOW = "quick_access_wallet_show"
        private const val DEFAULT_QUICK_ACCESS_WALLET_SHOW = true

        private const val KEY_QUICK_ACCESS_WALLET_SHOW_LOYALTY_CARDS = "quick_access_wallet_show_loyalty_cards"
        private const val DEFAULT_QUICK_ACCESS_WALLET_SHOW_LOYALTY_CARDS = true

        private const val KEY_QUICK_ACCESS_WALLET_SHOW_PREVIEW = "quick_access_wallet_show_preview"
        private const val DEFAULT_QUICK_ACCESS_SHOW_PREVIEW = false

        private const val KEY_QUICK_ACCESS_WALLET_ACCESS_WHILE_LOCKED = "quick_access_wallet_access_while_locked"
        private const val DEFAULT_QUICK_ACCESS_WALLET_ACCESS_WHILE_LOCKED = true

        private const val KEY_QUICK_ACCESS_WALLET_HIDE_CARD_NUMBER_WHEN_LOCKED = "quick_access_wallet_hide_card_number_when_locked"
        private const val DEFAULT_QUICK_ACCESS_WALLET_HIDE_CARD_NUMBER_WHEN_LOCKED = false

        private const val KEY_QUICK_ACCESS_WALLET_LOYALTY_HIDDEN_CARDS = "quick_access_wallet_hidden_cards"
        private val DEFAULT_QUICK_ACCESS_LOYALTY_WALLET_HIDDEN_CARDS = emptyList<String>()

        private const val KEY_QUICK_ACCESS_WALLET_LOYALTY_CARD_ORDER = "quick_access_wallet_card_order"
        private val DEFAULT_KEY_QUICK_ACCESS_WALLET_LOYALTY_CARD_ORDER = emptyList<String>()

        //Device Controls
        private const val KEY_DEVICE_CONTROLS_SHOW = "device_controls_show"
        private const val DEFAULT_DEVICE_CONTROLS_SHOW = true

        private const val KEY_DEVICE_CONTROLS_ALLOW_WHILE_LOCKED = "device_controls_allow_while_locked"
        private const val DEFAULT_DEVICE_CONTROLS_ALLOW_WHILE_LOCKED = true

        //Developer Options
        private const val KEY_DEVELOPER_OPTIONS_ENABLED = "developer_options_enabled"
        private const val DEFAULT_DEVELOPER_OPTIONS_ENABLED = false

        private const val KEY_DEVELOPER_CONTENT_CREATOR_MODE = "developer_content_creator_mode"
        private const val DEFAULT_DEVELOPER_CONTENT_CREATOR_MODE = false
    }

    override var hasSeenSetup by shared(KEY_HAS_SEEN_SETUP, DEFAULT_HAS_SEEN_SETUP)

    override var enabled by shared(KEY_ENABLED, DEFAULT_ENABLED)
    override val enabledFlow by lazy {
        sharedPreferences.getPreferenceAsFlow(KEY_ENABLED, ::enabled)
    }

    override var useMonet by shared(KEY_USE_MONET, DEFAULT_USE_MONET)
    override var monetColor by sharedColor(KEY_MONET_COLOR)
    override var useSolidBackground by shared(KEY_USE_SOLID_BACKGROUND, DEFAULT_USE_SOLID_BACKGROUND)

    override var powerOptionsHideWhenLocked by shared(KEY_POWER_OPTIONS_HIDE_WHEN_LOCKED, DEFAULT_POWER_OPTIONS_HIDE_WHEN_LOCKED)
    override var powerOptionsOpenCollapsed by shared(KEY_POWER_OPTIONS_OPEN_COLLAPSED, DEFAULT_POWER_OPTIONS_OPEN_COLLAPSED)
    override var powerMenuButtons by sharedList(KEY_POWER_MENU_BUTTONS, DEFAULT_POWER_MENU_BUTTONS, this::powerMenuButtonTypeConverter, this::powerMenuButtonTypeReverseConverter)
    override var allowRotation by shared(KEY_ALLOW_ROTATION, DEFAULT_ALLOW_ROTATION)
    override var allowFullRotation by shared(KEY_ALLOW_FULL_ROTATION, DEFAULT_ALLOW_FULL_ROTATION)

    override var quickAccessWalletShow by shared(KEY_QUICK_ACCESS_WALLET_SHOW, DEFAULT_QUICK_ACCESS_WALLET_SHOW)
    override var quickAccessWalletShowLoyaltyCards by shared(KEY_QUICK_ACCESS_WALLET_SHOW_LOYALTY_CARDS, DEFAULT_QUICK_ACCESS_WALLET_SHOW_LOYALTY_CARDS)
    override var quickAccessWalletShowPreview by shared(KEY_QUICK_ACCESS_WALLET_SHOW_PREVIEW, DEFAULT_QUICK_ACCESS_SHOW_PREVIEW)
    override var quickAccessWalletAccessWhileLocked by shared(KEY_QUICK_ACCESS_WALLET_ACCESS_WHILE_LOCKED, DEFAULT_QUICK_ACCESS_WALLET_ACCESS_WHILE_LOCKED)
    override var quickAccessWalletHideCardNumberWhenLocked by shared(KEY_QUICK_ACCESS_WALLET_HIDE_CARD_NUMBER_WHEN_LOCKED, DEFAULT_QUICK_ACCESS_WALLET_HIDE_CARD_NUMBER_WHEN_LOCKED)
    override val quickAccessWalletShowFlow by lazy {
        sharedPreferences.getPreferenceAsFlow(KEY_QUICK_ACCESS_WALLET_SHOW, ::quickAccessWalletShow)
    }
    override val quickAccessWalletShowLoyaltyCardsFlow by lazy {
        sharedPreferences.getPreferenceAsFlow(KEY_QUICK_ACCESS_WALLET_SHOW_LOYALTY_CARDS, ::quickAccessWalletShowLoyaltyCards)
    }

    override var quickAccessWalletLoyaltyCardsOrder by sharedList(KEY_QUICK_ACCESS_WALLET_LOYALTY_CARD_ORDER, DEFAULT_KEY_QUICK_ACCESS_WALLET_LOYALTY_CARD_ORDER, this::stringListTypeConverter, this::stringListTypeReverseConverter)
    override var quickAccessWalletLoyaltyCardsHidden by sharedList(KEY_QUICK_ACCESS_WALLET_LOYALTY_HIDDEN_CARDS, DEFAULT_QUICK_ACCESS_LOYALTY_WALLET_HIDDEN_CARDS, this::stringListTypeConverter, this::stringListTypeReverseConverter)
    override val quickAccessWalletLoyaltyCardsHiddenFlow by lazy {
        sharedPreferences.getPreferenceAsFlow(KEY_QUICK_ACCESS_WALLET_LOYALTY_CARD_ORDER, ::quickAccessWalletLoyaltyCardsHidden)
    }

    override var deviceControlsShow by shared(KEY_DEVICE_CONTROLS_SHOW, DEFAULT_DEVICE_CONTROLS_SHOW)
    override var deviceControlsAllowWhileLocked by shared(KEY_DEVICE_CONTROLS_ALLOW_WHILE_LOCKED, DEFAULT_DEVICE_CONTROLS_ALLOW_WHILE_LOCKED)

    override val deviceControlsShowFlow by lazy {
        sharedPreferences.getPreferenceAsFlow(KEY_DEVICE_CONTROLS_SHOW, ::deviceControlsShow)
    }

    override var developerOptionsEnabled by shared(KEY_DEVELOPER_OPTIONS_ENABLED, DEFAULT_DEVELOPER_OPTIONS_ENABLED)
    override var developerContentCreatorMode by shared(KEY_DEVELOPER_CONTENT_CREATOR_MODE, DEFAULT_DEVELOPER_CONTENT_CREATOR_MODE)

    override val developerOptionsEnabledFlow by lazy {
        sharedPreferences.getPreferenceAsFlow(KEY_DEVELOPER_OPTIONS_ENABLED, ::developerOptionsEnabled)
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("${BuildConfig.APPLICATION_ID}_prefs", Context.MODE_PRIVATE)
    }

    private fun shared(key: String, default: Boolean) = ReadWriteProperty({
        sharedPreferences.getBoolean(key, default)
    }, {
        sharedPreferences.edit().putBoolean(key, it).commit()
    })

    private fun shared(key: String, default: String) = ReadWriteProperty({
        sharedPreferences.getString(key, default) ?: default
    }, {
        sharedPreferences.edit().putString(key, it).commit()
    })

    private inline fun <reified T> sharedList(key: String, default: List<T>, crossinline transform: (List<T>) -> String, crossinline reverseTransform: (String) -> List<T>) = ReadWriteProperty({
        reverseTransform(sharedPreferences.getString(key, null) ?: transform(default))
    }, {
        sharedPreferences.edit().putString(key, transform(it)).commit()
    })

    private fun sharedColor(key: String) = ReadWriteProperty({
        val rawColor = sharedPreferences.getString(key, "") ?: ""
        if(rawColor.isEmpty()) null
        else Color.parseColor(rawColor)
    }, {
        sharedPreferences.edit().putString(key, it?.toHexString() ?: "").commit()
    })

    private inline fun <reified T : Enum<T>> sharedEnum(key: String, default: Enum<T>): ReadWriteProperty<Any?, T> {
        return object: ReadWriteProperty<Any?, T> {

            override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
                return java.lang.Enum.valueOf(T::class.java, sharedPreferences.getString(key, default.name))
            }

            override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                sharedPreferences.edit().putString(key, value.name).commit()
            }

        }
    }

    private inline fun <T> ReadWriteProperty(crossinline getValue: () -> T, crossinline setValue: (T) -> Unit): ReadWriteProperty<Any?, T> {
        return object: ReadWriteProperty<Any?, T> {

            override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
                return getValue.invoke()
            }

            override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                setValue.invoke(value)
            }

        }
    }

    private fun stringListTypeConverter(list: List<String>) : String {
        if(list.isEmpty()) return ""
        return list.joinToString(",")
    }

    private fun stringListTypeReverseConverter(pref: String): List<String> {
        if(pref.isEmpty()) return emptyList()
        if(!pref.contains(",")) return listOf(pref.trim())
        return pref.split(",")
    }

    private fun powerMenuButtonTypeConverter(list: List<PowerMenuButtonId>): String {
        if(list.isEmpty()) return ""
        return list.joinToString { it.name }
    }

    private fun powerMenuButtonTypeReverseConverter(pref: String): List<PowerMenuButtonId> {
        if(pref.isEmpty()) return emptyList()
        if(!pref.contains(",")) return listOf(PowerMenuButtonId.valueOf(pref.trim()))
        return pref.split(",").map {
            PowerMenuButtonId.valueOf(it.trim())
        }
    }

}
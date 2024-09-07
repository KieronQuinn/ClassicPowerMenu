package com.kieronquinn.app.classicpowermenu.components.settings

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButtonId
import com.kieronquinn.app.classicpowermenu.utils.extensions.createEncryptedSharedPrefDestructively
import com.kieronquinn.app.classicpowermenu.utils.extensions.toHexString
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class EncryptedSettings {

    abstract var aasToken: String
    abstract var walletToken: String

}

class EncryptedSettingsImpl(context: Context): EncryptedSettings() {

    companion object {

        private const val KEY_AAS_TOKEN = "aas_token"
        private const val KEY_WALLET_TOKEN = "wallet_token"

    }

    override var aasToken by shared(KEY_AAS_TOKEN, "")
    override var walletToken by shared(KEY_WALLET_TOKEN, "")

    private val sharedPreferences: SharedPreferences by lazy {
        context.createEncryptedSharedPrefDestructively("encrypted_shared_prefs")
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
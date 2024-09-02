package com.kieronquinn.app.classicpowermenu.components.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.components.settings.EncryptedSettingsImpl.Companion
import com.kieronquinn.app.classicpowermenu.utils.extensions.createEncryptedSharedPrefDestructively
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 *  Holds the encryption key and IV for the encrypted values in the Room database
 */
interface RoomEncryptedSettingsRepository {

    /**
     *  The encryption key used to encrypt sensitive data stored in the Room database.
     */
    fun getDatabaseEncryptionKey(): SecretKey

    /**
     *  The encryption IV used to encrypt sensitive data stored in the Room database.
     */
    fun getDatabaseEncryptionIV(): IvParameterSpec

    interface RoomEncryptionFailedCallback {
        fun onEncryptionFailed()
    }

}

class RoomEncryptedSettingsRepositoryImpl(
    context: Context,
    //failedCallback: RoomEncryptionFailedCallback?
): RoomEncryptedSettingsRepository {

    companion object {

        private const val KEY_ENCRYPTION_KEY = "encryption_key"
        private const val KEY_ENCRYPTION_IV = "encryption_iv"

    }

    var encryptionKey by shared(KEY_ENCRYPTION_KEY, "")
    var encryptionIV by shared(KEY_ENCRYPTION_IV, "")

    private val sharedPreferences: SharedPreferences by lazy {
        context.createEncryptedSharedPrefDestructively("${BuildConfig.APPLICATION_ID}_room_encrypted_shared_prefs")
    }

    @Synchronized
    override fun getDatabaseEncryptionKey(): SecretKey {
        return loadEncryptionKey() ?: saveEncryptionKey()
    }

    @Synchronized
    override fun getDatabaseEncryptionIV(): IvParameterSpec {
        return loadEncryptionIV() ?: saveEncryptionIV()
    }

    private fun loadEncryptionKey(): SecretKey? {
        val b64Key = encryptionKey
        if(b64Key.isEmpty()) return null
        val key = Base64.decode(b64Key, Base64.DEFAULT)
        return SecretKeySpec(key, "AES")
    }

    private fun saveEncryptionKey(): SecretKey {
        return KeyGenerator.getInstance("AES").apply {
            init(256)
        }.generateKey().also {
            encryptionKey = (Base64.encodeToString(it.encoded, Base64.DEFAULT))
        }
    }

    private fun loadEncryptionIV(): IvParameterSpec? {
        val b64IV = encryptionIV
        if(b64IV.isEmpty()) return null
        val iv = Base64.decode(b64IV, Base64.DEFAULT)
        return IvParameterSpec(iv)
    }

    private fun saveEncryptionIV(): IvParameterSpec {
        val bytes = ByteArray(16)
        SecureRandom().apply {
            nextBytes(bytes)
        }
        return IvParameterSpec(bytes).also {
            encryptionIV = (Base64.encodeToString(bytes, Base64.DEFAULT))
        }
    }

    private fun shared(key: String, default: String) = ReadWriteProperty({
        sharedPreferences.getString(key, default) ?: default
    }, {
        sharedPreferences.edit().putString(key, it).commit()
    })

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
}
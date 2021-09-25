package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet

import android.content.ComponentName
import android.content.pm.PackageManager
import com.kieronquinn.app.classicpowermenu.utils.extensions.isAppInstalled

object GooglePayConstants {

    const val WALLET_NFC_REL_PACKAGE_NAME = "com.google.android.apps.walletnfcrel"
    const val NBU_PAISA_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
    val WALLET_DEEP_LINK_COMPONENT = ComponentName(WALLET_NFC_REL_PACKAGE_NAME, "com.google.commerce.tapandpay.android.deeplink.DeepLinkActivity")
    const val WALLET_PREF_NAME = "global_prefs"
    const val WALLET_CURRENT_ACCOUNT_ID_PREF_KEY = "current_account_id"
    const val WALLET_DEEP_LINK_VALUABLE = "https://pay.google.com/gp/v/valuable/%s?vs=gp_lp"

    fun isGooglePayInstalled(packageManager: PackageManager): Boolean {
        return packageManager.isAppInstalled(WALLET_NFC_REL_PACKAGE_NAME)
    }

    fun isGPayInstalled(packageManager: PackageManager): Boolean {
        return packageManager.isAppInstalled(NBU_PAISA_PACKAGE_NAME)
    }

}
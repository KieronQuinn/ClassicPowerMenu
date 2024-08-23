package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.autoswitch

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.android.systemui.util.extensions.componentName
import com.kieronquinn.app.classicpowermenu.components.settings.Settings

import com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.autoswitchservice.SettingsQuickAccessWalletAutoSwitchServiceViewModel.AutoSwitchServiceItem
import kotlin.collections.ArrayList

interface AutoSwitchServicesRepository {

    fun getAutoSwitchServices(): ArrayList<AutoSwitchServiceItem>

}

class AutoSwitchServicesRepositoryImpl(private val context: Context, private val settings: Settings): AutoSwitchServicesRepository {
    override fun getAutoSwitchServices(): ArrayList<AutoSwitchServiceItem> {
        val list = ArrayList<AutoSwitchServiceItem>()

        val packageManager = context.packageManager
        val intent = Intent("android.nfc.cardemulation.action.HOST_APDU_SERVICE").addCategory(Intent.CATEGORY_DEFAULT)
        val resolveInfoList = packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resolveInfoList) {
            val service = AutoSwitchServiceItem(
                resolveInfo.serviceInfo.loadLabel(packageManager).toString(),
                resolveInfo.serviceInfo.componentName.flattenToString(),
                resolveInfo.serviceInfo.loadIcon(packageManager)
            )

            if (settings.quickAccessWalletSelectedAutoSwitchService == service.componentName) service.selected = true

            list.add(service)
        }

        return list
    }


}
package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.service.quickaccesswallet.QuickAccessWalletService
import android.util.Log

object CPMQuickAccessWalletServiceInfoBuilder {

    fun create(context: Context): Any? {
        val quickAccessWalletServiceInfoClass = Class.forName("android.service.quickaccesswallet.QuickAccessWalletServiceInfo")
        val quickAccessWalletServiceMetadataClass = Class.forName("android.service.quickaccesswallet.QuickAccessWalletServiceInfo\$ServiceMetadata")
        val defaultPaymentApp = getDefaultPaymentApp(context) ?: return null
        val serviceInfo = getWalletServiceInfo(context, defaultPaymentApp.packageName) ?: return null
        if (Manifest.permission.BIND_QUICK_ACCESS_WALLET_SERVICE != serviceInfo.permission) {
            Log.w("QAWalletSInfo", String.format("%s.%s does not require permission %s", serviceInfo.packageName, serviceInfo.name, Manifest.permission.BIND_QUICK_ACCESS_WALLET_SERVICE))
            return null
        }
        val metadata = quickAccessWalletServiceInfoClass.getDeclaredMethod("parseServiceMetadata", Context::class.java, ServiceInfo::class.java).apply {
            isAccessible = true
        }.invoke(null, context, serviceInfo)
        return quickAccessWalletServiceInfoClass.getDeclaredConstructor(ServiceInfo::class.java, quickAccessWalletServiceMetadataClass).apply {
            isAccessible = true
        }.newInstance(serviceInfo, metadata)
    }

    private fun getDefaultPaymentApp(context: Context): ComponentName? {
        val cr = context.contentResolver
        val comp =
            "com.google.android.gms/com.google.android.gms.tapandpay.hce.service.TpHceService" //TODO re-hook up Settings.Secure.getString(cr, "nfc_payment_default_component");
        return if (comp == null) null else ComponentName.unflattenFromString(comp)
    }

    private fun getWalletServiceInfo(context: Context, packageName: String): ServiceInfo? {
        val intent = Intent(QuickAccessWalletService.SERVICE_INTERFACE)
        intent.setPackage(packageName)
        val resolveInfos = context.packageManager.queryIntentServices(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return if (resolveInfos.isEmpty()) null else resolveInfos[0].serviceInfo
    }

}
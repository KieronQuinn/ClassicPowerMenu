package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

fun PackageManager.isAppInstalled(packageName: String): Boolean {
    return try {
        getPackageInfo(packageName, 0)
        true
    }catch (e: PackageManager.NameNotFoundException){
        false
    }
}

@Suppress("DEPRECATION")
fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo {
    return if (Build.VERSION.SDK_INT >= 33) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        getPackageInfo(packageName, flags)
    }
}
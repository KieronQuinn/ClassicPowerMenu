package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager

/**
 * Creates the [Intent] which can be used with [Context#startActivity(Intent)] to
 * launch the activity for emergency dialer.
 *
 * @param number Optional number to call in emergency dialer
 */
fun TelecomManager.createLaunchEmergencyDialerIntent(context: Context, number: String?): Intent {
    //OnePlus special case
    getOnePlusIntentIfValid(context)?.let {
        return it
    }
    return TelecomManager::class.java.getMethod("createLaunchEmergencyDialerIntent", String::class.java).invoke(this, number) as Intent
}

@SuppressLint("WrongConstant")
private fun getOnePlusIntentIfValid(context: Context): Intent? {
    val intent = Intent("oneplus.telephony.action.EMERGENCY_ASSISTANCE")
    intent.addFlags(0x14800000)
    intent.putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 2)
    return if(context.packageManager.resolveActivity(intent, 0) != null) intent
    else null
}
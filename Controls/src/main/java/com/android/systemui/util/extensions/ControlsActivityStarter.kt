package com.android.systemui.util.extensions

import android.content.Intent

interface ControlsActivityStarter {

    fun startActivity(intent: Intent, dismissPowerMenu: Boolean = true)
    fun dismissPowerMenuActivity()
    fun runAfterKeyguardDismissed(onDismissAction: () -> Boolean, onCancel: (() -> Unit)?, afterDismissed: Boolean, dismissPowerMenu: Boolean = true)

}
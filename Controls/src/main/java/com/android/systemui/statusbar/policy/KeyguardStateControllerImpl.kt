package com.android.systemui.statusbar.policy

import android.app.KeyguardManager
import android.content.Context

class KeyguardStateControllerImpl(context: Context, private val allowWhileLocked: () -> Boolean): KeyguardStateController {

    private val keyguardManager by lazy {
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }

    override fun addCallback(listener: KeyguardStateController.Callback?) {
        throw UnsupportedOperationException("addCallback is not supported for KeyguardStateController")
    }

    override fun removeCallback(listener: KeyguardStateController.Callback?) {
        throw UnsupportedOperationException("addCallback is not supported for KeyguardStateController")
    }

    override fun isShowing(): Boolean {
        return keyguardManager.isDeviceLocked && !allowWhileLocked()
    }

    override fun canDismissLockScreen(): Boolean {
        return keyguardManager.isDeviceLocked
    }
}
package com.kieronquinn.app.classicpowermenu.components.controls

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.systemui.util.extensions.ControlsActivityStarter
import com.kieronquinn.app.classicpowermenu.components.navigation.PowerMenuNavigation
import com.kieronquinn.app.classicpowermenu.utils.extensions.getTopActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ControlsActivityStarterImpl(private val context: Context, private val lifecycleScope: CoroutineScope, private val navigation: PowerMenuNavigation): ControlsActivityStarter {

    private val keyguardManager by lazy {
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }

    override fun startActivity(intent: Intent, dismissPowerMenu: Boolean) {
        if(dismissPowerMenu){
            dismissPowerMenu()
        }
        context.startActivity(intent)
    }

    override fun dismissPowerMenuActivity() {
        dismissPowerMenu()
    }

    override fun runAfterKeyguardDismissed(
        onDismissAction: () -> Boolean,
        onCancel: (() -> Unit)?,
        afterDismissed: Boolean,
        dismissPowerMenu: Boolean
    ) {
        if(!afterDismissed || !keyguardManager.isDeviceLocked){
            onDismissAction()
            return
        }

        getTopActivity()?.let {
            keyguardManager.requestDismissKeyguard(it, object: KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissSucceeded() {
                    super.onDismissSucceeded()
                    onDismissAction()
                }

                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                    onCancel?.invoke()
                }
            })
        }


    }

    private fun dismissPowerMenu(){
        lifecycleScope.launch {
            navigation.closePowerMenu()
        }
    }

}
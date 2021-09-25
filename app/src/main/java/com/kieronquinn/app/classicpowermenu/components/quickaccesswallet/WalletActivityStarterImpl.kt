package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet

import com.android.systemui.plugins.ActivityStarter
import com.android.systemui.plugins.activitystarter.WalletActivityStarter
import com.kieronquinn.app.classicpowermenu.components.navigation.PowerMenuNavigation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalletActivityStarterImpl(private val activityStarter: ActivityStarter, private val navigation: PowerMenuNavigation): WalletActivityStarter {

    override fun runAfterKeyguardDismissed(runnable: Runnable) {
        activityStarter.dismissKeyguardThenExecute({
            GlobalScope.launch {
                navigation.closePowerMenu()
            }
            runnable.run()
            true
        }, null, false)
    }

}
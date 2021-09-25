package com.android.systemui.plugins.activitystarter

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface WalletActivityStarter {

    fun runAfterKeyguardDismissed(runnable: Runnable)

}

object WalletActivityStarterProvider: KoinComponent {

    private val starter by inject<WalletActivityStarter>()

    fun getActivityStarter(): WalletActivityStarter {
        return starter
    }

}
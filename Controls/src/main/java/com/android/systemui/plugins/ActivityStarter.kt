package com.android.systemui.plugins

import com.android.systemui.util.extensions.ControlsActivityStarter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ActivityStarter: KoinComponent {

    private val controlsActivityStarter by inject<ControlsActivityStarter>()

    fun dismissKeyguardThenExecute(onDismissAction: () -> Boolean, cancel: (() -> Unit)?, afterKeyguardGone: Boolean, dismissPowerMenu: Boolean = true) {
        controlsActivityStarter.runAfterKeyguardDismissed(onDismissAction, cancel, afterKeyguardGone, dismissPowerMenu)
    }

    fun dismissPowerMenu(){
        controlsActivityStarter.dismissPowerMenuActivity()
    }

}
package com.android.systemui.globalactions

import com.android.systemui.util.extensions.ServiceRunner

class GlobalActionsComponent(private val serviceRunner: ServiceRunner) {
    fun handleShowGlobalActionsMenu() {
        serviceRunner.showPowerMenu()
    }
}

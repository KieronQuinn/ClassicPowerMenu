package com.kieronquinn.app.classicpowermenu.service.globalactions

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.kieronquinn.app.classicpowermenu.IGlobalActions
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.components.starter.PowerMenuStarter
import org.koin.android.ext.android.inject

class GlobalActionsService: LifecycleService() {

    private val starter by inject<PowerMenuStarter>()
    private val settings by inject<Settings>()

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return GlobalActionsServiceImpl()
    }

    private inner class GlobalActionsServiceImpl: IGlobalActions.Stub() {

        override fun showGlobalActions(): Boolean {
            if(!settings.enabled) return false
            starter.onPowerButtonLongPressed()
            return true
        }

        override fun hideGlobalActions() {
            if(!settings.enabled) return
            starter.hideGlobalActions()
        }

    }

}
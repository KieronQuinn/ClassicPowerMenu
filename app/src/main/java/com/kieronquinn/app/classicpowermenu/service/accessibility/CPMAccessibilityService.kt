package com.kieronquinn.app.classicpowermenu.service.accessibility

import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.coroutineScope
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.components.starter.PowerMenuStarter
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.utils.AccessibilityLifecycleService
import com.kieronquinn.app.classicpowermenu.utils.extensions.addFromSelf
import com.kieronquinn.app.classicpowermenu.utils.extensions.sendBroadcast
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.components.xposed.XposedSelfHook
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class CPMAccessibilityService: AccessibilityLifecycleService() {

    companion object {
        const val INTENT_ACTION_BRING_TO_FRONT = "${BuildConfig.APPLICATION_ID}.ACTION_BRING_TO_FRONT"
    }

    private val settings by inject<Settings>()
    private val starter by inject<PowerMenuStarter>()
    private val service by inject<CPMServiceContainer>()

    private val selfHook = XposedSelfHook()

    override fun onCreate() {
        super.onCreate()
        sendBroadcast(Intent(INTENT_ACTION_BRING_TO_FRONT).apply {
            `package` = packageName
        })
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.className?.toString()?.contains("globalactions") == true && settings.enabled && !selfHook.isXposedHooked()){
            if(starter.onPowerButtonLongPressed()){
                sendClose()
            }
        }
    }

    private fun sendClose() = lifecycle.coroutineScope.launch {
        service.runWithService {
            it.sendBroadcast(this@CPMAccessibilityService, Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS).apply {
                addFromSelf()
            })
        }
    }

    override fun onInterrupt() {}

}
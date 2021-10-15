package com.kieronquinn.app.classicpowermenu.service.runner

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.android.systemui.util.extensions.ServiceRunner
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.utils.extensions.bindService
import com.kieronquinn.app.classicpowermenu.utils.extensions.getTopActivity
import com.kieronquinn.app.classicpowermenu.utils.extensions.startActivity
import com.kieronquinn.app.classicpowermenu.utils.extensions.unbindService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CPMServiceRunner(private val container: CPMServiceContainer, private val lifecycleScope: CoroutineScope): ServiceRunner {

    override fun bindService(context: Context, intent: Intent, serviceConnection: ServiceConnection, flags: Int) {
        lifecycleScope.launch {
            container.runWithService {
                //If activity has gone we can't recover as we need the token
                val activity = getTopActivity() ?: return@runWithService
                it.bindService(activity, intent, serviceConnection, flags, false)
            }
        }
    }

    override fun unbindService(context: Context, serviceConnection: ServiceConnection) {
        lifecycleScope.launch {
            container.runWithService {
                it.unbindService(serviceConnection)
            }
        }
    }

    override fun getIntentForPendingIntent(
        pendingIntent: PendingIntent,
        callback: (Intent) -> Unit
    ) {
        lifecycleScope.launch {
            container.runWithService {
                val intent = it.getIntentForPendingIntent(pendingIntent)
                callback(intent)
            }
        }
    }

    override fun startActivity(context: Context, intent: Intent, runAfter: (() -> Unit)?) {
        lifecycleScope.launch {
            container.runWithService {
                it.startActivity(context, intent, true)
            }
        }
    }

    override fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {
        getTopActivity()?.overridePendingTransition(enterAnim, exitAnim)
    }

    override fun getCurrentUser(): Int {
        return container.runWithServiceIfAvailable {
            it.currentUser
        } ?: 0
    }

    override fun showPowerMenu() {
        lifecycleScope.launch {
            container.runWithService {
                it.showPowerMenu()
            }
        }
    }

}
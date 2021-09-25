package com.kieronquinn.app.classicpowermenu.components.starter

import android.content.Context
import android.content.Intent
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.ui.activities.PowerMenuActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface PowerMenuStarter {

    fun onPowerButtonLongPressed(): Boolean
    fun hideGlobalActions(): Boolean

    fun setEventListener(listener: PowerMenuStarterEventListener?)

    interface PowerMenuStarterEventListener {
        fun onPowerButtonLongPressed(): Boolean
    }

}

class PowerMenuStarterImpl(context: Context, private val service: CPMServiceContainer): PowerMenuStarter {

    private val defaultStarter = {
        GlobalScope.launch {
            service.runWithService {
                it.resumeAppSwitches()
            }
            context.startActivity(Intent(context, PowerMenuActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
        }

    }

    private var eventListener: PowerMenuStarter.PowerMenuStarterEventListener? = null

    override fun setEventListener(listener: PowerMenuStarter.PowerMenuStarterEventListener?) {
        this.eventListener = listener
    }

    override fun onPowerButtonLongPressed(): Boolean {
        return eventListener?.onPowerButtonLongPressed() ?: run {
            defaultStarter()
            true
        }
    }

    override fun hideGlobalActions(): Boolean {
        //Only call long pressed if it's already showing
        return eventListener?.onPowerButtonLongPressed() ?: false
    }

}
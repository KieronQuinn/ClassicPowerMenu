package com.kieronquinn.app.classicpowermenu.ui.activities

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.utils.extensions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class PowerMenuActivityViewModel: ViewModel() {

    abstract val closeBroadcast: Flow<Unit>
    abstract fun sendCloseBroadcast(context: Context, ignoreSelf: Boolean)

}

class PowerMenuActivityViewModelImpl(context: Context, private val service: CPMServiceContainer): PowerMenuActivityViewModel() {

    private companion object {
        private val CLOSE_ACTIONS = arrayOf(Intent.ACTION_SCREEN_OFF, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    }

    override val closeBroadcast = context.broadcastReceiverAsFlow(*CLOSE_ACTIONS).mapNotNull {
        if(it.isFromSelf()) null
        else Unit
    }

    override fun sendCloseBroadcast(context: Context, ignoreSelf: Boolean) {
        viewModelScope.launch {
            service.runWithService {
                it.sendBroadcast(context, Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS).apply {
                    if(ignoreSelf) addFromSelf()
                })
            }
        }
    }

}
package com.kieronquinn.app.classicpowermenu.ui.screens.decision

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class DecisionViewModel: ViewModel() {
    abstract val decisionMade: Flow<Unit>
}

class DecisionViewModelImpl(private val containerNavigation: ContainerNavigation, private val settings: Settings): DecisionViewModel() {

    private val _decisionMade = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            containerNavigation.navigate(getDestination())
            emit(Unit)
        }
    }

    override val decisionMade = _decisionMade.asSharedFlow()

    private fun getDestination(): NavDirections {
        return if(settings.hasSeenSetup){
            DecisionFragmentDirections.actionDecisionFragmentToNavGraphSettingsContainer()
        }else{
            DecisionFragmentDirections.actionDecisionFragmentToNavGraphSetup()
        }
    }

}
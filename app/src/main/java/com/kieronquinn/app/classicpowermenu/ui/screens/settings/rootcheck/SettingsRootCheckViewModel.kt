package com.kieronquinn.app.classicpowermenu.ui.screens.settings.rootcheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class SettingsRootCheckViewModel: ViewModel() {

    abstract val state: Flow<State>

    abstract fun showSettings()
    abstract fun showNoRoot()

    sealed class State {
        object CheckingRoot: State()
        data class Result(val result: RootResult): State()
    }

    enum class RootResult {
        ROOTED, NOT_ROOTED
    }

}

class SettingsRootCheckViewModelImpl(private val containerNavigation: ContainerNavigation): SettingsRootCheckViewModel() {

    private val _state = flow<State> {
        val isRooted = Shell.cmd("whoami").exec().out.firstOrNull() == "root"
        if(isRooted){
            emit(State.Result(RootResult.ROOTED))
        }else{
            emit(State.Result(RootResult.NOT_ROOTED))
        }
    }.flowOn(Dispatchers.IO)

    override val state = _state.onStart { emit(State.CheckingRoot) }

    override fun showSettings() {
        viewModelScope.launch {
            containerNavigation.navigate(SettingsRootCheckFragmentDirections.actionSettingsRootCheckFragmentToSettingsContainerFragment())
        }
    }

    override fun showNoRoot() {
        viewModelScope.launch {
            containerNavigation.navigate(SettingsRootCheckFragmentDirections.actionSettingsRootCheckFragmentToSettingsRootCheckNoRootBottomSheetFragment())
        }
    }

}
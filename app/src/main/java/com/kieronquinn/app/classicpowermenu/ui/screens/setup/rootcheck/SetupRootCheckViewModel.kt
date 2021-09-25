package com.kieronquinn.app.classicpowermenu.ui.screens.setup.rootcheck

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.service.accessibility.CPMAccessibilityService
import com.kieronquinn.app.classicpowermenu.utils.extensions.isAccessibilityServiceEnabled
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

abstract class SetupRootCheckViewModel: ViewModel() {

    abstract fun showNext(context: Context)
    abstract fun showNoRoot()

    abstract val state: Flow<State>

    sealed class State {
        object CheckingRoot: State()
        data class Result(val result: RootResult): State()
    }

    enum class RootResult {
        ROOTED, NOT_ROOTED
    }

}

class SetupRootCheckViewModelImpl(private val navigation: ContainerNavigation): SetupRootCheckViewModel(){

    private val _state = flow<State> {
        val isRooted = Shell.rootAccess()
        if(isRooted){
            emit(State.Result(RootResult.ROOTED))
        }else{
            emit(State.Result(RootResult.NOT_ROOTED))
        }
    }.flowOn(Dispatchers.IO)

    override val state = _state.onStart { emit(State.CheckingRoot) }

    override fun showNext(context: Context) {
        viewModelScope.launch {
            if(isAccessibilityServiceEnabled(context, CPMAccessibilityService::class.java)){
                navigation.navigate(SetupRootCheckFragmentDirections.actionSetupRootCheckFragmentToSetupWalletFragment())
            }else{
                navigation.navigate(SetupRootCheckFragmentDirections.actionSetupRootCheckFragmentToSetupAccessibilityFragment())
            }
        }
    }

    override fun showNoRoot() {
        viewModelScope.launch {
            navigation.navigate(SetupRootCheckFragmentDirections.actionSetupRootCheckFragmentToSettingsRootCheckNoRootBottomSheetFragment2())
        }
    }

}
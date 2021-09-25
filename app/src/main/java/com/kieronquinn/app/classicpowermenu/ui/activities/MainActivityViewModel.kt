package com.kieronquinn.app.classicpowermenu.ui.activities

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.github.UpdateChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class MainActivityViewModel: ViewModel() {

    abstract val appReady: StateFlow<Boolean>
    abstract fun onDecisionMade()

    abstract val update: Flow<UpdateChecker.Update?>
    abstract fun getAvailableUpdate(): UpdateChecker.Update?
    abstract fun clearUpdate()

}

class MainActivityViewModelImpl(context: Context, private val updateChecker: UpdateChecker): MainActivityViewModel() {

    private val decisionMade = MutableSharedFlow<Unit>()

    private val _update = MutableStateFlow<UpdateChecker.Update?>(null).apply {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                updateChecker.clearCachedDownloads(context)
            }
            updateChecker.getLatestRelease().collect {
                emit(it)
            }
        }
    }

    private val splashDelay = flow {
        delay(1000)
        emit(true)
    }

    private val _appReady = combine(splashDelay, decisionMade){ requirements, _ ->
        requirements
    }

    override val appReady = MutableStateFlow(false).apply {
        viewModelScope.launch {
            _appReady.collect { emit(it) }
        }
    }

    override fun onDecisionMade() {
        viewModelScope.launch {
            decisionMade.emit(Unit)
        }
    }

    override val update = _update.asStateFlow()

    override fun getAvailableUpdate(): UpdateChecker.Update? {
        return _update.value
    }

    override fun clearUpdate() {
        viewModelScope.launch {
            _update.emit(null)
        }
    }


}
package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.manage

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.GoogleApiRepository
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.GoogleWalletRepository
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.WalletLoyaltyCardViewInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class SettingsQuickAccessWalletManageViewModel: ViewModel() {

    abstract val state: MutableStateFlow<State>

    abstract fun onCardVisibilityClicked(card: RearrangeLoyaltyCard)
    abstract fun onCardOrderChanged(cards: List<RearrangeLoyaltyCard>)
    abstract fun onBackPressed()
    abstract fun onSignInClicked()
    abstract fun onPageLoad()
    abstract fun syncLoyaltyCards()

    sealed class State {
        object Loading: State()
        object SignInRequired: State()
        data class Loaded(val cards: List<RearrangeLoyaltyCard>): State()
        data class Error(val type: ErrorType): State()
    }

    data class RearrangeLoyaltyCard(var visible: Boolean, val info: WalletLoyaltyCardViewInfo) {
        val id = info.id
    }

    enum class ErrorType(@StringRes val contentRes: Int) {
        ERROR(R.string.settings_quick_access_wallet_manage_error_generic), NO_CARDS(R.string.settings_quick_access_wallet_manage_error_no_cards)
    }
}

class SettingsQuickAccessWalletManageViewModelImpl(private val walletRepository: GoogleWalletRepository, private val googleApiRepository: GoogleApiRepository, private val settings: Settings, private val containerNavigation: ContainerNavigation): SettingsQuickAccessWalletManageViewModel() {

    override val state = MutableStateFlow<State>(State.Loading)

    override fun syncLoyaltyCards() {
        viewModelScope.launch {
            state.emit(State.Loading)
            walletRepository.syncValuables()
            updateLoyaltyCards()
        }
    }

    override fun onPageLoad() {
        viewModelScope.launch {
            if (!googleApiRepository.isSignedIn()) return@launch state.emit(State.SignInRequired)
            updateLoyaltyCards()
        }
    }

    private suspend fun updateLoyaltyCards() {
        val cards = walletRepository.getLoyaltyCards({ false }, true)?.map {
            it as WalletLoyaltyCardViewInfo
        }

        if (cards == null) return state.emit(State.Error(ErrorType.ERROR))
        if (cards.isEmpty()) return state.emit(State.Error(ErrorType.NO_CARDS))

        val order = settings.quickAccessWalletLoyaltyCardsOrder
        val hidden = settings.quickAccessWalletLoyaltyCardsHidden
        val ordered = cards.sortedBy {
            order.indexOf(it.id)
        }.map {
            RearrangeLoyaltyCard(!hidden.contains(it.id), it)
        }
        state.emit(State.Loaded(ordered))
    }

    override fun onCardVisibilityClicked(card: RearrangeLoyaltyCard) {
        viewModelScope.launch {
            if(card.visible){
                settings.quickAccessWalletLoyaltyCardsHidden = settings.quickAccessWalletLoyaltyCardsHidden.toMutableSet().apply {
                    remove(card.id)
                }.toList()
            }else{
                settings.quickAccessWalletLoyaltyCardsHidden = settings.quickAccessWalletLoyaltyCardsHidden.toMutableSet().apply {
                    add(card.id)
                }.toList()
            }
        }
    }

    override fun onCardOrderChanged(cards: List<RearrangeLoyaltyCard>) {
        viewModelScope.launch {
            settings.quickAccessWalletLoyaltyCardsOrder = cards.map { it.id }
        }
    }

    override fun onSignInClicked() {
        viewModelScope.launch {
            containerNavigation.navigate(SettingsQuickAccessWalletManageFragmentDirections.actionSettingsQuickAccessWalletManageFragmentToSettingsQuickAccessWalletSignInToGoogle())
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

}
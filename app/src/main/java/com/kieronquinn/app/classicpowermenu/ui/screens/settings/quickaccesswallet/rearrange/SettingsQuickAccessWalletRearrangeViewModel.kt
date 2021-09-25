package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.rearrange

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.LoyaltyCardsRepository
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.WalletLoyaltyCardViewInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

abstract class SettingsQuickAccessWalletRearrangeViewModel: ViewModel() {

    abstract val state: Flow<State>

    abstract fun onCardVisibilityClicked(card: RearrangeLoyaltyCard)
    abstract fun onCardOrderChanged(cards: List<RearrangeLoyaltyCard>)
    abstract fun onBackPressed()

    sealed class State {
        object Loading: State()
        data class Loaded(val cards: List<RearrangeLoyaltyCard>): State()
        data class Error(val type: ErrorType): State()
    }

    data class RearrangeLoyaltyCard(var visible: Boolean, val info: WalletLoyaltyCardViewInfo) {
        val id = info.id
    }

    enum class ErrorType(@StringRes val contentRes: Int) {
        ERROR(R.string.settings_quick_access_wallet_rearrange_error_generic), NO_CARDS(R.string.settings_quick_access_wallet_rearrange_error_no_cards)
    }

}

class SettingsQuickAccessWalletRearrangeViewModelImpl(private val loyaltyCardsRepository: LoyaltyCardsRepository, private val settings: Settings, private val containerNavigation: ContainerNavigation): SettingsQuickAccessWalletRearrangeViewModel() {

    private val loyaltyCards = flow {
        val cards = loyaltyCardsRepository.getLoyaltyCards({ false }, true)
        emit(cards?.map { it as WalletLoyaltyCardViewInfo })
    }

    private val _state = combine(loyaltyCards, settings.quickAccessWalletLoyaltyCardsHiddenFlow){ cards: List<WalletLoyaltyCardViewInfo>?, hiddenIds: List<String> ->
        when {
            cards == null -> {
                State.Error(ErrorType.ERROR)
            }
            cards.isEmpty() -> {
                State.Error(ErrorType.NO_CARDS)
            }
            else -> {
                val order = settings.quickAccessWalletLoyaltyCardsOrder
                State.Loaded(cards.sortedBy { order.indexOf(it.id) }.map { RearrangeLoyaltyCard(!hiddenIds.contains(it.id), it) })
            }
        }
    }

    override val state = _state.onStart { emit(State.Loading) }

    override fun onCardVisibilityClicked(card: RearrangeLoyaltyCard) {
        viewModelScope.launch {
            if(card.visible){
                settings.quickAccessWalletLoyaltyCardsHidden = settings.quickAccessWalletLoyaltyCardsHidden.toMutableList().apply {
                    remove(card.id)
                }
            }else{
                settings.quickAccessWalletLoyaltyCardsHidden = settings.quickAccessWalletLoyaltyCardsHidden.toMutableList().apply {
                    add(card.id)
                }
            }
        }
    }

    override fun onCardOrderChanged(cards: List<RearrangeLoyaltyCard>) {
        viewModelScope.launch {
            settings.quickAccessWalletLoyaltyCardsOrder = cards.map { it.id }
        }
    }

    override fun onBackPressed() {
        viewModelScope.launch {
            containerNavigation.navigateBack()
        }
    }

}
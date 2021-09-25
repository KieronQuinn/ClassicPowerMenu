package com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.android.systemui.controls.ui.ControlsUiController
import com.android.systemui.plugin.globalactions.wallet.WalletPanelViewController
import com.kieronquinn.app.classicpowermenu.databinding.ItemPowerMenuCardsBinding
import com.kieronquinn.app.classicpowermenu.databinding.ItemPowerMenuControlsBinding
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuContentItem
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuContentItemType

class PowerMenuContentAdapter(context: Context, private var items: List<PowerMenuContentItem>, val dismissGlobalActionsMenu: () -> Unit, var walletViewController: WalletPanelViewController?, var controlsUiController: ControlsUiController?): RecyclerView.Adapter<PowerMenuContentAdapter.ViewHolder>() {

    fun notifyItemChangeOfType(type: PowerMenuContentItem){
        val index = items.indexOf(type)
        if(index == -1) return
        notifyItemChanged(index)
    }

    private val layoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].itemType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(PowerMenuContentItemType.values()[viewType]){
            PowerMenuContentItemType.CARDS -> ViewHolder.Cards(
                ItemPowerMenuCardsBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            PowerMenuContentItemType.CONTROLS -> ViewHolder.Controls(
                ItemPowerMenuControlsBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is ViewHolder.Cards -> holder.setupCards()
            is ViewHolder.Controls -> holder.setupControls()
        }
    }

    private fun ViewHolder.Cards.setupCards() = with(binding.itemPowerMenuCardsContainer) {
        removeAllViews()
        walletViewController?.let {
            it.queryWalletCards()
            addView(it.panelContent)
        }
    }

    private fun ViewHolder.Controls.setupControls() = with(binding.itemPowerMenuControlsContainer) {
        removeAllViews()
        controlsUiController?.hide()
        controlsUiController?.show(this) {
            dismissGlobalActionsMenu()
        }
    }

    sealed class ViewHolder(open val binding: ViewBinding): RecyclerView.ViewHolder(binding.root){
        data class Cards(override val binding: ItemPowerMenuCardsBinding): ViewHolder(binding)
        data class Controls(override val binding: ItemPowerMenuControlsBinding): ViewHolder(binding)
    }

}
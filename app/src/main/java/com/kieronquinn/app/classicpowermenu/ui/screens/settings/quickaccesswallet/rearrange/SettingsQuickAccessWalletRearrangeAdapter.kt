package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.rearrange

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.ItemWalletRearrangeCardBinding
import com.kieronquinn.monetcompat.core.MonetCompat

class SettingsQuickAccessWalletRearrangeAdapter(context: Context, var cards: List<SettingsQuickAccessWalletRearrangeViewModel.RearrangeLoyaltyCard>, val onCardVisibilityToggleClicked: (SettingsQuickAccessWalletRearrangeViewModel.RearrangeLoyaltyCard) -> Unit, val onHandleLongPress: (ViewHolder) -> Unit): RecyclerView.Adapter<SettingsQuickAccessWalletRearrangeAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    private val layoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getItemId(position: Int): Long {
        return cards[position].id.hashCode().toLong()
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWalletRearrangeCardBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cards[position]
        val card = item.info.getLoyaltyCard()
        with(holder.binding){
            walletRearrangeCardTitle.text = card.issuerName
            walletRearrangeCardText.text = card.title
            walletRearrangeCardVisibilityToggle.imageTintList = ColorStateList.valueOf(monet.getAccentColor(walletRearrangeCardVisibilityToggle.context))
            walletRearrangeCardDragHandle.imageTintList = ColorStateList.valueOf(monet.getAccentColor(walletRearrangeCardDragHandle.context))
            if(item.visible){
                walletRearrangeCardVisibilityToggle.setImageResource(R.drawable.ic_quick_access_wallet_visibility_visible)
            }else{
                walletRearrangeCardVisibilityToggle.setImageResource(R.drawable.ic_quick_access_wallet_visibility_invisible)
            }
            walletRearrangeCardVisibilityToggle.setOnClickListener {
                item.visible = !item.visible
                onCardVisibilityToggleClicked(item)
                notifyItemChanged(position)
            }
            walletRearrangeCardDragHandle.setOnLongClickListener {
                onHandleLongPress(holder)
                true
            }
            walletRearrangeCardCard.clipToOutline = true
            walletRearrangeCardCard.setImageDrawable(item.info.cardDrawable)
        }
    }

    data class ViewHolder(val binding: ItemWalletRearrangeCardBinding): RecyclerView.ViewHolder(binding.root)

}
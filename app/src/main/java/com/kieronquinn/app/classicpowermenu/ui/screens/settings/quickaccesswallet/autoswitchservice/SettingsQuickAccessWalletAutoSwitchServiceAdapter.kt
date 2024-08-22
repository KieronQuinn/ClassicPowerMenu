package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.autoswitch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.app.classicpowermenu.databinding.ItemWalletSelectServiceCardBinding
import com.kieronquinn.monetcompat.core.MonetCompat

class SettingsQuickAccessWalletChangeDefaultPaymentMethodAdapter(context: Context, var cards: List<SettingsQuickAccessWalletChangeDefaultPaymentMethodViewModel.SelectAutoSwitchService>, val onServiceClicked: (SettingsQuickAccessWalletChangeDefaultPaymentMethodViewModel.SelectAutoSwitchService) -> Unit): RecyclerView.Adapter<SettingsQuickAccessWalletChangeDefaultPaymentMethodAdapter.ViewHolder>() {

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
        return cards[position].componentName.hashCode().toLong()
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWalletSelectServiceCardBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cards[position]
        with(holder.binding){
            walletSelectServiceCardTitle.text = item.name
            walletSelectServiceCardText.text = item.componentName
            walletSelectServiceCardImage.clipToOutline = true
            walletSelectServiceCardImage.setImageDrawable(item.image)
        }

        if (item.selected) holder.itemView.setBackgroundColor(monet.getSecondaryColor(holder.itemView.context))

        holder.itemView.setOnClickListener {
            onServiceClicked(item)
        }
    }

    data class ViewHolder(val binding: ItemWalletSelectServiceCardBinding): RecyclerView.ViewHolder(binding.root)

}
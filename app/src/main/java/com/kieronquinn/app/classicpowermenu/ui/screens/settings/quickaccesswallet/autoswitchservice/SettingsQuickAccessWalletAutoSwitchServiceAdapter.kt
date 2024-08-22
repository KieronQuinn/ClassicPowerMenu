package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.autoswitchservice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.app.classicpowermenu.databinding.ItemWalletServiceBinding
import com.kieronquinn.monetcompat.core.MonetCompat

class SettingsQuickAccessWalletAutoSwitchServiceAdapter(context: Context, var services: List<SettingsQuickAccessWalletAutoSwitchServiceViewModel.AutoSwitchServiceItem>, val onServiceClicked: (SettingsQuickAccessWalletAutoSwitchServiceViewModel.AutoSwitchServiceItem) -> Unit): RecyclerView.Adapter<SettingsQuickAccessWalletAutoSwitchServiceAdapter.ViewHolder>() {

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
        return services[position].componentName.hashCode().toLong()
    }

    override fun getItemCount() = services.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWalletServiceBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = services[position]
        with(holder.binding){
            walletServiceTitle.text = item.name
            walletServiceText.text = item.componentName
            walletServiceImage.clipToOutline = true
            walletServiceImage.setImageDrawable(item.image)
        }

        if (item.selected) holder.itemView.setBackgroundColor(monet.getSecondaryColor(holder.itemView.context))

        holder.itemView.setOnClickListener {
            onServiceClicked(item)
        }
    }

    data class ViewHolder(val binding: ItemWalletServiceBinding): RecyclerView.ViewHolder(binding.root)

}
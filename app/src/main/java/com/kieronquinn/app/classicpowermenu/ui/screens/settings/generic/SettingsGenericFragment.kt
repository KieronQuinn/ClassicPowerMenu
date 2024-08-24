package com.kieronquinn.app.classicpowermenu.ui.screens.settings.generic

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsGenericBinding
import com.kieronquinn.app.classicpowermenu.model.settings.SettingsItem
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import com.kieronquinn.monetcompat.extensions.views.enableStretchOverscroll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

abstract class SettingsGenericBaseFragment<T: ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T): BoundFragment<T>(inflate) {

    //Lazy causes layout issues with RecyclerView
    abstract val recyclerView: () -> RecyclerView
    abstract val items: List<SettingsItem>
    open val applyStretchOverscrollCompat = true
    open val adapterEnabled: Flow<Boolean>? = null
    open val adapterEnabledInitialState: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupInsets()
    }

    private fun setupRecyclerView() = with(recyclerView()) {
        layoutManager = LinearLayoutManager(context)
        adapter = SettingsGenericAdapter(context, items, adapterEnabledInitialState ?: true).apply {
            setup()
        }
        if(applyStretchOverscrollCompat){
            enableStretchOverscroll()
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
        }
    }

    private fun SettingsGenericAdapter.setup() = adapterEnabled?.let {
        whenResumed {
            it.collect { enabled ->
                isAdapterEnabled = enabled
                notifyDataSetChanged()
            }
        }
    }

    private fun setupInsets(){
        binding.root.onApplyInsets { view, insets ->
            val bottomPadding = resources.getDimension(R.dimen.margin_16).toInt()
            view.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom + bottomPadding)
        }
    }

}

abstract class SettingsGenericFragment: SettingsGenericBaseFragment<FragmentSettingsGenericBinding>(FragmentSettingsGenericBinding::inflate) {

    override val recyclerView = { binding.root }

}
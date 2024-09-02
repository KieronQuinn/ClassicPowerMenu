package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.rearrange

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsQuickAccessWalletRearrangeBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SettingsQuickAccessWalletRearrangeFragment: BoundFragment<FragmentSettingsQuickAccessWalletRearrangeBinding>(FragmentSettingsQuickAccessWalletRearrangeBinding::inflate), StandaloneFragment {

    private val adapter by lazy {
        SettingsQuickAccessWalletRearrangeAdapter(requireContext(), emptyList(), viewModel::onCardVisibilityClicked){
            itemTouchHelper.startDrag(it)
        }
    }

    private val itemTouchHelper by lazy {
        ItemTouchHelper(ItemTouchCallback())
    }

    private val viewModel by viewModel<SettingsQuickAccessWalletRearrangeViewModel>()

    override val onBackPressed by lazy {
        return@lazy viewModel::onBackPressed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupState()
        setupMonet()
        setupInsets()

        binding.button.setOnClickListener {
            viewModel.tempSync()
        }
    }

    private fun setupMonet(){
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
        binding.quickAccessWalletRearrangeLoadingBar.applyMonet()
    }

    private fun setupState() = whenResumed {
        viewModel.state.collect {
            handleState(it)
        }
    }

    private fun handleState(state: SettingsQuickAccessWalletRearrangeViewModel.State){
        when(state){
            is SettingsQuickAccessWalletRearrangeViewModel.State.Loading -> {
                binding.quickAccessWalletRearrangeRecyclerview.isVisible = false
                binding.quickAccessWalletRearrangeEmpty.isVisible = false
                binding.quickAccessWalletRearrangeLoading.isVisible = true
            }
            is SettingsQuickAccessWalletRearrangeViewModel.State.Error -> {
                binding.quickAccessWalletRearrangeRecyclerview.isVisible = false
                binding.quickAccessWalletRearrangeEmpty.isVisible = true
                binding.quickAccessWalletRearrangeEmptyText.setText(state.type.contentRes)
                binding.quickAccessWalletRearrangeLoading.isVisible = false
            }
            is SettingsQuickAccessWalletRearrangeViewModel.State.Loaded -> {
                binding.quickAccessWalletRearrangeRecyclerview.isVisible = true
                binding.quickAccessWalletRearrangeEmpty.isVisible = false
                binding.quickAccessWalletRearrangeLoading.isVisible = false
                adapter.cards = state.cards
            }
        }
    }

    private fun setupToolbar(){
        with(binding.toolbar){
            setupWithScrollableView(binding.quickAccessWalletRearrangeRecyclerview)
            setNavigationOnClickListener {
                viewModel.onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() = with(binding.quickAccessWalletRearrangeRecyclerview) {
        layoutManager = LinearLayoutManager(context)
        adapter = this@SettingsQuickAccessWalletRearrangeFragment.adapter
        itemTouchHelper.attachToRecyclerView(this)
    }

    private fun setupInsets(){
        binding.toolbar.onApplyInsets { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = topInset)
        }
        binding.quickAccessWalletRearrangeRecyclerview.onApplyInsets { view, insets ->
            val bottomPadding = resources.getDimension(R.dimen.margin_16).toInt()
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = bottomPadding + bottomInset)
        }
    }

    private inner class ItemTouchCallback: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.bindingAdapterPosition
            val toPosition = target.bindingAdapterPosition
            Collections.swap(adapter.cards, fromPosition, toPosition)
            adapter.notifyItemMoved(fromPosition, toPosition)
            viewModel.onCardOrderChanged(adapter.cards)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //no-op
        }

        override fun isLongPressDragEnabled() = false

    }

}
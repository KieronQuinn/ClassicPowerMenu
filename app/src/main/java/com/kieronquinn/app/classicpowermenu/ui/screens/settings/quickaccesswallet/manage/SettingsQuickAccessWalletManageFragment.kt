package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.manage

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsQuickAccessWalletManageBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.isDarkMode
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SettingsQuickAccessWalletManageFragment: BoundFragment<FragmentSettingsQuickAccessWalletManageBinding>(FragmentSettingsQuickAccessWalletManageBinding::inflate), StandaloneFragment {

    private val adapter by lazy {
        SettingsQuickAccessWalletManageAdapter(requireContext(), emptyList(), viewModel::onCardVisibilityClicked){
            itemTouchHelper.startDrag(it)
        }
    }

    private val itemTouchHelper by lazy {
        ItemTouchHelper(ItemTouchCallback())
    }

    private val viewModel by viewModel<SettingsQuickAccessWalletManageViewModel>()

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
        setUpClickListeners()
        viewModel.onPageLoad()
    }

    private fun setUpClickListeners() {
        binding.signInButton.setOnClickListener {
            viewModel.onSignInClicked()
        }

        binding.syncButton.setOnClickListener {
            viewModel.syncLoyaltyCards()
        }
    }

    private fun setupMonet(){
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
        binding.loadingBar.applyMonet()
        binding.syncButton.applyMonet()
        binding.signInButton.applyMonet()
        binding.signInButton.setTextColor(monet.getPrimaryColor(requireContext()))
        binding.syncButton.drawable.setTint(monet.getPrimaryColor(requireContext()))
        binding.signInInfoCard.run {
            val background = monet.getPrimaryColor(context, !context.isDarkMode)
            backgroundTintList = ColorStateList.valueOf(background)
        }
    }

    private fun setupState() = whenResumed {
        viewModel.state.collect {
            handleState(it)
        }
    }

    private fun handleState(state: SettingsQuickAccessWalletManageViewModel.State){
        when(state){
            is SettingsQuickAccessWalletManageViewModel.State.Loading -> {
                binding.recyclerview.isVisible = false
                binding.error.isVisible = false
                binding.signIn.isVisible = false
                binding.syncButtonContainer.isVisible = false

                binding.loading.isVisible = true
            }
            is SettingsQuickAccessWalletManageViewModel.State.SignInRequired -> {
                binding.recyclerview.isVisible = false
                binding.error.isVisible = false
                binding.loading.isVisible = false
                binding.syncButtonContainer.isVisible = false

                binding.signIn.isVisible = true
            }
            is SettingsQuickAccessWalletManageViewModel.State.Error -> {
                binding.recyclerview.isVisible = false
                binding.loading.isVisible = false
                binding.signIn.isVisible = false

                binding.error.isVisible = true
                binding.errorText.setText(state.type.contentRes)

                when (state.type) {
                    SettingsQuickAccessWalletManageViewModel.ErrorType.ERROR -> binding.syncButtonContainer.isVisible = false
                    SettingsQuickAccessWalletManageViewModel.ErrorType.NO_CARDS -> binding.syncButtonContainer.isVisible = true
                }
            }
            is SettingsQuickAccessWalletManageViewModel.State.Loaded -> {
                binding.error.isVisible = false
                binding.loading.isVisible = false
                binding.signIn.isVisible = false

                binding.recyclerview.isVisible = true
                binding.syncButtonContainer.isVisible = true
                adapter.cards = state.cards

                binding.syncButtonContainer.bringToFront()
            }
        }
    }

    private fun setupToolbar(){
        with(binding.toolbar){
            setupWithScrollableView(binding.recyclerview)
            setNavigationOnClickListener {
                viewModel.onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() = with(binding.recyclerview) {
        layoutManager = LinearLayoutManager(context)
        adapter = this@SettingsQuickAccessWalletManageFragment.adapter
        itemTouchHelper.attachToRecyclerView(this)
    }

    private fun setupInsets(){
        binding.toolbar.onApplyInsets { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = topInset)
        }
        binding.signIn.onApplyInsets { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = topInset)
        }
        binding.recyclerview.onApplyInsets { view, insets ->
            val bottomPadding = resources.getDimension(R.dimen.margin_16).toInt()
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = bottomPadding + bottomInset)
        }
        binding.syncButtonContainer.onApplyInsets { view, insets ->
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
            adapter.notifyItemChanged(fromPosition)
            adapter.notifyItemChanged(toPosition)
            viewModel.onCardOrderChanged(adapter.cards)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //no-op
        }

        override fun isLongPressDragEnabled() = false

    }

}
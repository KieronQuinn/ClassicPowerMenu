package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.autoswitch

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsQuickAccessWalletChangeDefaultPaymentMethodBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsQuickAccessWalletChangeDefaultPaymentMethodFragment: BoundFragment<FragmentSettingsQuickAccessWalletChangeDefaultPaymentMethodBinding>(FragmentSettingsQuickAccessWalletChangeDefaultPaymentMethodBinding::inflate), StandaloneFragment {

    private val adapter by lazy {
        SettingsQuickAccessWalletChangeDefaultPaymentMethodAdapter(requireContext(), emptyList(), viewModel::onServiceClicked)
    }

    private val viewModel by viewModel<SettingsQuickAccessWalletChangeDefaultPaymentMethodViewModel>()

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
    }

    private fun setupState() = lifecycleScope.launchWhenResumed {
        viewModel.state.collect {
            handleState(it)
        }
    }

    private fun handleState(state: SettingsQuickAccessWalletChangeDefaultPaymentMethodViewModel.State){
        when(state){
            is SettingsQuickAccessWalletChangeDefaultPaymentMethodViewModel.State.Loading -> {
                binding.quickAccessWalletChangeDefaultPaymentMethodRecyclerview.isVisible = false
                binding.quickAccessWalletAutoSwitchServiceLoading.isVisible = true
            }
            is SettingsQuickAccessWalletChangeDefaultPaymentMethodViewModel.State.Error -> {
                //binding.quickAccessWalletRearrangeRecyclerview.isVisible = false
                //binding.quickAccessWalletRearrangeEmpty.isVisible = true
                //binding.quickAccessWalletRearrangeEmptyText.setText(state.type.contentRes)
                //binding.quickAccessWalletRearrangeLoading.isVisible = false
            }
            is SettingsQuickAccessWalletChangeDefaultPaymentMethodViewModel.State.Loaded -> {
                binding.quickAccessWalletChangeDefaultPaymentMethodRecyclerview.isVisible = true
                binding.quickAccessWalletAutoSwitchServiceLoading.isVisible = false
                adapter.cards = state.services
            }
        }
    }



    private fun setupToolbar(){
        with(binding.toolbar){
            setupWithScrollableView(binding.quickAccessWalletChangeDefaultPaymentMethodRecyclerview)
            setNavigationOnClickListener {
                viewModel.onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() = with(binding.quickAccessWalletChangeDefaultPaymentMethodRecyclerview) {
        layoutManager = LinearLayoutManager(context)
        adapter = this@SettingsQuickAccessWalletChangeDefaultPaymentMethodFragment.adapter
    }

    private fun setupMonet(){
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
        binding.quickAccessWalletAutoSwitchServiceLoadingBar.applyMonet()
    }

    private fun setupInsets(){
        binding.toolbar.onApplyInsets { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = topInset)
        }
        binding.quickAccessWalletChangeDefaultPaymentMethodRecyclerview.onApplyInsets { view, insets ->
            val bottomPadding = resources.getDimension(R.dimen.margin_16).toInt()
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = bottomPadding + bottomInset)
        }
    }

}
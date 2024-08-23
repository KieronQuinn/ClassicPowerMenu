package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.autoswitchservice

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsQuickAccessWalletAutoSwitchServiceBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsQuickAccessWalletAutoSwitchServiceFragment: BoundFragment<FragmentSettingsQuickAccessWalletAutoSwitchServiceBinding>(FragmentSettingsQuickAccessWalletAutoSwitchServiceBinding::inflate), StandaloneFragment {

    private val adapter by lazy {
        SettingsQuickAccessWalletAutoSwitchServiceAdapter(requireContext(), emptyList(), viewModel::onServiceClicked)
    }

    private val viewModel by viewModel<SettingsQuickAccessWalletAutoSwitchServiceViewModel>()

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

    private fun handleState(state: SettingsQuickAccessWalletAutoSwitchServiceViewModel.State){
        when(state){
            is SettingsQuickAccessWalletAutoSwitchServiceViewModel.State.Loading -> {
                binding.quickAccessWalletAutoSwitchServiceRecyclerview.isVisible = false
                binding.quickAccessWalletAutoSwitchServiceEmpty.isVisible = false

                binding.quickAccessWalletAutoSwitchServiceLoading.isVisible = true
            }
            is SettingsQuickAccessWalletAutoSwitchServiceViewModel.State.Error -> {
                binding.quickAccessWalletAutoSwitchServiceRecyclerview.isVisible = false
                binding.quickAccessWalletAutoSwitchServiceLoading.isVisible = false

                binding.quickAccessWalletAutoSwitchServiceEmptyText.setText(state.type.contentRes)
                binding.quickAccessWalletAutoSwitchServiceEmpty.isVisible = true
            }
            is SettingsQuickAccessWalletAutoSwitchServiceViewModel.State.Loaded -> {
                binding.quickAccessWalletAutoSwitchServiceLoading.isVisible = false
                binding.quickAccessWalletAutoSwitchServiceEmpty.isVisible = false

                binding.quickAccessWalletAutoSwitchServiceRecyclerview.isVisible = true
                adapter.services = state.services
            }
        }
    }



    private fun setupToolbar(){
        with(binding.toolbar){
            setupWithScrollableView(binding.quickAccessWalletAutoSwitchServiceRecyclerview)
            setNavigationOnClickListener {
                viewModel.onBackPressed()
            }
        }
    }

    private fun setupRecyclerView() = with(binding.quickAccessWalletAutoSwitchServiceRecyclerview) {
        layoutManager = LinearLayoutManager(context)
        adapter = this@SettingsQuickAccessWalletAutoSwitchServiceFragment.adapter
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
        binding.quickAccessWalletAutoSwitchServiceRecyclerview.onApplyInsets { view, insets ->
            val bottomPadding = resources.getDimension(R.dimen.margin_16).toInt()
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = bottomPadding + bottomInset)
        }
    }

}
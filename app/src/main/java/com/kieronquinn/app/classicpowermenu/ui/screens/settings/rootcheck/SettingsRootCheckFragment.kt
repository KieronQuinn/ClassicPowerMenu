package com.kieronquinn.app.classicpowermenu.ui.screens.settings.rootcheck

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsRootCheckBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsRootCheckFragment: BoundFragment<FragmentSettingsRootCheckBinding>(FragmentSettingsRootCheckBinding::inflate) {

    private val viewModel by viewModel<SettingsRootCheckViewModel>()

    override val disableAllAnimation = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonet()
        setupState()
    }

    private fun setupMonet(){
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
        binding.rootCheckProgress.applyMonet()
    }

    private fun setupState() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.state.collect {
                handleState(it)
            }
        }
    }

    private fun handleState(state: SettingsRootCheckViewModel.State){
        if(state is SettingsRootCheckViewModel.State.Result){
            if(state.result == SettingsRootCheckViewModel.RootResult.ROOTED){
                viewModel.showSettings()
            }else{
                viewModel.showNoRoot()
            }
        }
    }

}
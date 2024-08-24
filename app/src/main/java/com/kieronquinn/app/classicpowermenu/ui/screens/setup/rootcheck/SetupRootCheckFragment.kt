package com.kieronquinn.app.classicpowermenu.ui.screens.setup.rootcheck

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSetupRootcheckBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupRootCheckFragment: BoundFragment<FragmentSetupRootcheckBinding>(FragmentSetupRootcheckBinding::inflate) {

    private val viewModel by viewModel<SetupRootCheckViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonet()
        setupState()
    }

    private fun setupMonet(){
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
        binding.setupRootcheckProgress.applyMonet()
    }

    private fun setupState() {
        whenResumed {
            viewModel.state.collect {
                handleState(it)
            }
        }
    }

    private fun handleState(state: SetupRootCheckViewModel.State){
        if(state is SetupRootCheckViewModel.State.Result){
            if(state.result == SetupRootCheckViewModel.RootResult.ROOTED){
                viewModel.showNext(requireContext())
            }else{
                viewModel.showNoRoot()
            }
        }
    }

}
package com.kieronquinn.app.classicpowermenu.ui.screens.decision

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.ui.activities.MainActivityViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DecisionFragment: Fragment() {

    private val viewModel by viewModel<DecisionViewModel>()
    private val appViewModel by sharedViewModel<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupAppReady()
        return View(context)
    }

    private fun setupAppReady() = viewLifecycleOwner.lifecycleScope.launchWhenResumed {
        viewModel.decisionMade.collect {
            appViewModel.onDecisionMade()
        }
    }

}
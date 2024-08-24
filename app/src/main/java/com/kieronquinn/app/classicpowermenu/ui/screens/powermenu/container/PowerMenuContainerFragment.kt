package com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.container

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.NavigationEvent
import com.kieronquinn.app.classicpowermenu.components.navigation.PowerMenuNavigation
import com.kieronquinn.app.classicpowermenu.databinding.FragmentPowerMenuContainerBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.navigateSafely
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class PowerMenuContainerFragment: BoundFragment<FragmentPowerMenuContainerBinding>(FragmentPowerMenuContainerBinding::inflate) {

    private val navigation by inject<PowerMenuNavigation>()

    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() = whenResumed {
        navigation.navigationBus.collect {
            handleNavigationEvent(it)
        }
    }

    private fun handleNavigationEvent(event: NavigationEvent) {
        when(event){
            is NavigationEvent.Directions -> navController.navigateSafely(event.directions)
            is NavigationEvent.Id -> navController.navigateSafely(event.id)
            is NavigationEvent.Intent -> requireActivity().startActivity(event.intent)
            is NavigationEvent.Back -> navController.navigateUp()
            is NavigationEvent.PopupTo -> navController.popBackStack(event.id, event.popInclusive)
        }
    }

}
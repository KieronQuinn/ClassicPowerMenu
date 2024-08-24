package com.kieronquinn.app.classicpowermenu.ui.screens.settings.root

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.components.navigation.NavigationEvent
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsRootBinding
import com.kieronquinn.app.classicpowermenu.ui.activities.MainActivityViewModel
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.rootcheck.SettingsRootCheckFragmentDirections
import com.kieronquinn.app.classicpowermenu.utils.extensions.navigateSafely
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsRootFragment: BoundFragment<FragmentSettingsRootBinding>(FragmentSettingsRootBinding::inflate) {

    private val navigation by inject<ContainerNavigation>()
    private val sharedViewModel by sharedViewModel<MainActivityViewModel>()

    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment_root) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupUpdateChecker()
    }

    private fun setupNavigation() = whenResumed {
        navigation.navigationBus.collect {
            handleNavigationEvent(it)
        }
    }

    private fun handleNavigationEvent(event: NavigationEvent){
        when(event) {
            is NavigationEvent.Directions -> navController.navigateSafely(event.directions)
            is NavigationEvent.Id -> navController.navigateSafely(event.id)
            is NavigationEvent.PopupTo -> navController.popBackStack(event.id, event.popInclusive)
            is NavigationEvent.Back -> navController.navigateUp()
            is NavigationEvent.Intent -> startActivity(event.intent)
        }
    }

    private fun setupUpdateChecker(){
        return
        whenResumed {
            sharedViewModel.update.collect {
                if(it != null) {
                    navigation.navigate(SettingsRootCheckFragmentDirections.actionGlobalUpdateAvailableBottomSheetFragment())
                }
            }
        }
    }

}
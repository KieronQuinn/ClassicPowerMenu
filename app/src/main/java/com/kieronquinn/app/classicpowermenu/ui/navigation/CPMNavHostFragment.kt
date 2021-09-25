package com.kieronquinn.app.classicpowermenu.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.kieronquinn.app.classicpowermenu.components.navigation.TransitionedActivityNavigator

class CPMNavHostFragment: NavHostFragment() {

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)
        navController.navigatorProvider.addNavigator(TransitionedActivityNavigator(requireActivity()))
    }

}
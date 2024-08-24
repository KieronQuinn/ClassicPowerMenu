package com.kieronquinn.app.classicpowermenu.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.viewbinding.ViewBinding
import autoCleared
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import com.kieronquinn.app.classicpowermenu.utils.TransitionUtils
import com.kieronquinn.monetcompat.app.MonetFragment
import org.koin.android.ext.android.inject

abstract class BoundFragment<T: ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T): MonetFragment() {

    internal var binding by autoCleared<T>()
    private val containerNavigation by inject<ContainerNavigation>()

    open val disableEnterExitAnimation = false
    open val disableAllAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!disableAllAnimation) {
            if(!disableEnterExitAnimation) {
                exitTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), true)
                enterTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), true)
            }
            returnTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), false)
            reenterTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val wrappedInflater = inflater.cloneInContext(requireContext())
        binding = inflate.invoke(wrappedInflater, container, false)
        return binding.root
    }

}
package com.kieronquinn.app.classicpowermenu.ui.screens.settings.switched

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsSwitchedBinding
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.generic.SettingsGenericBaseFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

abstract class SettingsSwitchedFragment: SettingsGenericBaseFragment<FragmentSettingsSwitchedBinding>(FragmentSettingsSwitchedBinding::inflate) {

    abstract val switchText: CharSequence
    abstract val onSwitchClicked: () -> Unit
    abstract val switchStartState: Boolean
    abstract val switchChecked: Flow<Boolean>

    override val recyclerView = { binding.settingsSwitchedRecyclerview }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSwitchBackground()
        setupSwitch()
    }

    private fun setupSwitchBackground() = with(binding.settingsSwitchedSwitchBackground){
        setBackgroundColor(monet.getBackgroundColor(requireContext()))
    }

    private fun setupSwitch() = with(binding.settingsSwitchedSwitch) {
        text = switchText
        setOnClickListener {
            onSwitchClicked()
        }
        isChecked = switchStartState
        lifecycleScope.launchWhenResumed {
            switchChecked.collect {
                isChecked = it
            }
        }
    }

}
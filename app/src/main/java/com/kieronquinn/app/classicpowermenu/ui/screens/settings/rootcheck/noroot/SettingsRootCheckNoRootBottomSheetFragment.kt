package com.kieronquinn.app.classicpowermenu.ui.screens.settings.rootcheck.noroot

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.kieronquinn.app.classicpowermenu.databinding.FragmentBottomSheetRootCheckNoRootBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BaseBottomSheetFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets

class SettingsRootCheckNoRootBottomSheetFragment: BaseBottomSheetFragment<FragmentBottomSheetRootCheckNoRootBinding>(FragmentBottomSheetRootCheckNoRootBinding::inflate) {

    override val cancelable = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootCheckNoRootClose.setOnClickListener {
            requireActivity().finish()
        }
        binding.root.onApplyInsets { view, insets ->
            view.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom)
        }
        binding.rootCheckNoRootClose.setTextColor(monet.getAccentColor(requireContext()))
    }

}
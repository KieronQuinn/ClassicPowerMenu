package com.kieronquinn.app.classicpowermenu.ui.screens.setup.complete

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSetupCompleteBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.monetcompat.extensions.views.overrideRippleColor
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupCompleteFragment: BoundFragment<FragmentSetupCompleteBinding>(FragmentSetupCompleteBinding::inflate), StandaloneFragment {

    private val viewModel by viewModel<SetupCompleteViewModel>()

    override val onBackPressed by lazy {
        return@lazy viewModel::onBackPressed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonet()
        setupInsets()
        setupToolbar()
        setupNext()
    }

    override fun onResume() {
        super.onResume()
        binding.setupCompleteImage.run {
            frame = 0
            playAnimation()
        }
    }

    private fun setupMonet() {
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
    }

    private fun setupToolbar(){
        binding.toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }
    }

    private fun setupInsets(){
        binding.root.onApplyInsets { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(top = topInset, bottom = bottomInset)
        }
    }

    private fun setupNext(){
        binding.setupCompleteButton.run {
            val accentColor = monet.getAccentColor(requireContext())
            strokeColor = ColorStateList.valueOf(accentColor)
            setTextColor(accentColor)
            overrideRippleColor(accentColor)
            setOnClickListener {
                viewModel.onFinishClicked()
            }
        }
    }

}
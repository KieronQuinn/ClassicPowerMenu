package com.kieronquinn.app.classicpowermenu.ui.screens.setup.landing

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSetupLandingBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.monetcompat.extensions.views.overrideRippleColor
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupLandingFragment: BoundFragment<FragmentSetupLandingBinding>(FragmentSetupLandingBinding::inflate) {

    private val viewModel by viewModel<SetupLandingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonet()
        setupInsets()
        setupGetStarted()
    }

    private fun setupMonet() {
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
        (binding.setupLandingLogo.background as GradientDrawable).setColor(monet.getPrimaryColor(requireContext(), false))
        binding.setupLandingButton.run {
            val accentColor = monet.getAccentColor(requireContext())
            strokeColor = ColorStateList.valueOf(accentColor)
            setTextColor(accentColor)
            overrideRippleColor(accentColor)
        }
    }

    private fun setupInsets(){
        binding.setupLandingButton.onApplyInsets { view, insets ->
            val bottomInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val bottomMargin = resources.getDimension(R.dimen.margin_16).toInt() * 2
            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                updateMargins(bottom = bottomInsets + bottomMargin)
            }
        }
    }

    private fun setupGetStarted(){
        binding.setupLandingButton.setOnClickListener {
            viewModel.onGetStartedClicked()
        }
    }

}
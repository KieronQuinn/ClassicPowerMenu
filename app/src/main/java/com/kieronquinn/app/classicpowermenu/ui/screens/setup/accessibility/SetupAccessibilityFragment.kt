package com.kieronquinn.app.classicpowermenu.ui.screens.setup.accessibility

import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSetupAccessibilityBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.monetcompat.extensions.views.overrideRippleColor
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupAccessibilityFragment: BoundFragment<FragmentSetupAccessibilityBinding>(FragmentSetupAccessibilityBinding::inflate), StandaloneFragment {

    private val viewModel by viewModel<SetupAccessibilityViewModel>()

    override val onBackPressed by lazy {
        return@lazy viewModel::onBackPressed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonet()
        setupInsets()
        setupText()
        setupToolbar()
        setupImage()
        setupEnable()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun setupText(){
        binding.setupAccessibilityContent.run {
            setLinkTextColor(monet.getAccentColor(requireContext()))
            movementMethod = BetterLinkMovementMethod.getInstance().apply {
                setOnClickListener {
                    viewModel.onSkipClicked()
                }
            }
        }
    }

    private fun setupMonet(){
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
        binding.setupAccessibilityButton.run {
            val accentColor = monet.getAccentColor(requireContext())
            strokeColor = ColorStateList.valueOf(accentColor)
            setTextColor(accentColor)
            overrideRippleColor(accentColor)
        }
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

    private fun setupImage(){
        (binding.setupAccessibilityImage.drawable as AnimatedVectorDrawable).start()
    }

    private fun setupEnable() {
        binding.setupAccessibilityButton.setOnClickListener {
            viewModel.onEnableClicked()
        }
    }

}
package com.kieronquinn.app.classicpowermenu.ui.screens.setup.wallet

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSetupWalletBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import com.kieronquinn.monetcompat.extensions.views.overrideRippleColor
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupWalletFragment: BoundFragment<FragmentSetupWalletBinding>(FragmentSetupWalletBinding::inflate), StandaloneFragment {

    private val viewModel by viewModel<SetupWalletViewModel>()

    override val onBackPressed by lazy {
        return@lazy viewModel::onBackPressed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonet()
        setupInsets()
        setupToolbar()
        setupSwitch()
        setupNext()
    }

    override fun onResume() {
        super.onResume()
        binding.setupWalletImage.run {
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

    private fun setupSwitch(){
        with(binding.setupWalletSwitch){
            isChecked = viewModel.walletEnabled
            setOnClickListener {
                viewModel.onWalletSwitchClicked()
            }
            whenResumed {
                viewModel.walletEnabledFlow.collect {
                    isChecked = it
                }
            }
        }
    }

    private fun setupNext(){
        binding.setupWalletButton.run {
            val accentColor = monet.getAccentColor(requireContext())
            strokeColor = ColorStateList.valueOf(accentColor)
            setTextColor(accentColor)
            overrideRippleColor(accentColor)
            setOnClickListener {
                viewModel.onNextClicked()
            }
        }
    }

}
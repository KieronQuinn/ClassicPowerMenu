package com.kieronquinn.app.classicpowermenu.ui.screens.settings.update

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentUpdateBottomSheetBinding
import com.kieronquinn.app.classicpowermenu.ui.activities.MainActivityViewModel
import com.kieronquinn.app.classicpowermenu.ui.base.BaseBottomSheetFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateAvailableBottomSheetFragment: BaseBottomSheetFragment<FragmentUpdateBottomSheetBinding>(FragmentUpdateBottomSheetBinding::inflate) {

    private val sharedViewModel by sharedViewModel<MainActivityViewModel>()
    private val viewModel by viewModel<UpdateAvailableBottomSheetViewModel>()

    private val update by lazy {
        sharedViewModel.getAvailableUpdate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.onApplyInsets { _, insets ->
            val bottomPadding = resources.getDimension(R.dimen.margin_16).toInt()
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = bottomInset + bottomPadding)
        }
        binding.updateBottomSheetContent.text = update?.changelog
        val accent = monet.getAccentColor(requireContext())
        binding.updateBottomSheetPositive.setTextColor(accent)
        binding.updateBottomSheetNegative.setTextColor(accent)
        binding.updateBottomSheetNeutral.setTextColor(accent)
        binding.updateBottomSheetPositive.setOnClickListener {
            viewModel.onDownloadClicked()
        }
        binding.updateBottomSheetNegative.setOnClickListener {
            sharedViewModel.clearUpdate()
            viewModel.onDismissClicked()
        }
        binding.updateBottomSheetNeutral.setOnClickListener {
            update?.let {
                viewModel.onOpenInGitHubClicked(it)
            }
        }
    }

}
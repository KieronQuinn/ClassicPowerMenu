package com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions.rearrange

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsPowerOptionsRearrangeBinding
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButton
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.StandaloneFragment
import com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.main.PowerMenuButtonsAdapter
import com.kieronquinn.app.classicpowermenu.utils.extensions.*
import com.kieronquinn.monetcompat.extensions.views.enableStretchOverscroll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Integer.min

class SettingsPowerOptionsRearrangeFragment: BoundFragment<FragmentSettingsPowerOptionsRearrangeBinding>(FragmentSettingsPowerOptionsRearrangeBinding::inflate), StandaloneFragment {

    private val viewModel by viewModel<SettingsPowerOptionsRearrangeViewModel>()

    private val powerMenuButtonsWorkspace by lazy {
        viewModel.loadPowerMenuButtons(requireContext()).toMutableList()
    }

    private val powerMenuButtonsOptions by lazy {
        viewModel.loadUnusedPowerMenuButtons(requireContext()).toMutableList()
    }

    private val workspaceLayoutManager by lazy {
        FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
    }

    private val optionsLayoutManager by lazy {
        FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
    }

    private val workspaceAdapter by lazy {
        PowerMenuButtonsAdapter(
            requireContext(),
            powerMenuButtonsWorkspace,
            viewModel.monetEnabled,
            this::showSnackbar,
            isWorkspace = true
        )
    }

    private val optionsAdapter by lazy {
        PowerMenuButtonsAdapter(
            requireContext(),
            powerMenuButtonsOptions,
            viewModel.monetEnabled,
            this::showSnackbar,
            setEmptyStateVisible = {
                binding.settingsPowerOptionsRearrangeNoItems.isVisible = it
            }
        )
    }

    override val onBackPressed by lazy {
        return@lazy viewModel::onBackPressed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupInsets()
        setupScroll()
        setupWorkspace()
        setupInfo()
        setupOptions()
        setupDragListener()
    }

    private fun setupToolbar() = with(binding.toolbar){
        setupWithScrollableView(binding.powerOptionsRearrangeScrollview)
        setNavigationOnClickListener {
            viewModel.onBackPressed()
        }
        background = ColorDrawable(monet.getBackgroundColor(context))
    }

    private fun setupInsets(){
        binding.toolbar.onApplyInsets { view, insets ->
            val topInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = topInsets)
        }
        binding.powerOptionsRearrangeScrollview.onApplyInsets { view, insets ->
            val bottomInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = bottomInsets)
        }
    }

    private fun setupScroll() = with(binding.powerOptionsRearrangeScrollview) {
        enableStretchOverscroll()
    }

    private fun setupWorkspace() = with(binding.powerOptionsRearrangeWorkspace) {
        layoutManager = workspaceLayoutManager
        adapter = workspaceAdapter
        background = ColorDrawable(monet.getBackgroundColor(context))
        binding.monetBackgroundFix.setBackgroundColor(monet.getBackgroundColor(context))
        whenResumed {
            workspaceAdapter.changed.debounce(100).collect {
                viewModel.savePowerMenuButtons(it)
            }
        }
    }

    private fun setupInfo() = with(binding.powerOptionsRearrangeInfo){
        backgroundTintList = ColorStateList.valueOf(monet.getBackgroundColor(context))
    }

    private fun setupOptions() = with(binding.powerOptionsRearrangeOptions) {
        layoutManager = optionsLayoutManager
        adapter = optionsAdapter
    }
    
    private fun showSnackbar(text: CharSequence){
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

    private fun setupDragListener(){
        workspaceAdapter.setupDrag(binding.powerOptionsRearrangeWorkspace, viewLifecycleOwner)
        optionsAdapter.setupDrag(binding.powerOptionsRearrangeOptions, viewLifecycleOwner)
    }

}
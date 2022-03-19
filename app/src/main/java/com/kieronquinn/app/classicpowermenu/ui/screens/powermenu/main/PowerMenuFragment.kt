package com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.main

import android.animation.ValueAnimator
import android.app.KeyguardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.service.quickaccesswallet.CPMQuickAccessWalletClientImpl
import android.view.Surface
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.view.DisplayCutoutCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.systemui.controls.ui.ControlsUiController
import com.android.systemui.plugin.globalactions.wallet.WalletPanelViewController
import com.android.systemui.plugins.GlobalActionsPanelPlugin
import com.android.systemui.plugins.loyaltycards.WalletLoyaltyCardCallback
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kieronquinn.app.classicpowermenu.ClassicPowerMenu
import com.kieronquinn.app.classicpowermenu.IClassicPowerMenu
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.databinding.FragmentPowerMenuBinding
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuButton
import com.kieronquinn.app.classicpowermenu.model.power.PowerMenuContentItem
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class PowerMenuFragment :
    BoundFragment<FragmentPowerMenuBinding>(FragmentPowerMenuBinding::inflate),
    GlobalActionsPanelPlugin.Callbacks {

    private val serviceContainer by inject<CPMServiceContainer>()
    private val controlsUiController by inject<ControlsUiController>()

    private val viewModel by viewModel<PowerMenuViewModel>()

    private val keyguardManager by lazy {
        requireContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }

    private val powerMenuApplication by lazy {
        requireActivity().application as ClassicPowerMenu
    }

    private val powerMenuButtons by lazy {
        viewModel.loadPowerMenuButtons(requireContext()).toMutableList()
    }

    private val contentAdapter by lazy {
        PowerMenuContentAdapter(
            requireContext(),
            getItems(),
            this::dismissGlobalActionsMenu,
            null,
            null
        )
    }

    private fun getItems(): List<PowerMenuContentItem> {
        return ArrayList<PowerMenuContentItem>().apply {
            if(viewModel.showQuickAccessWallet) add(PowerMenuContentItem.Cards)
            if(viewModel.showControls) add(PowerMenuContentItem.Controls)
        }
    }

    private val buttonsAdapter by lazy {
        PowerMenuButtonsAdapter(
            requireContext(),
            emptyList<PowerMenuButton>().toMutableList(),
            viewModel.monetEnabled)
    }

    private val buttonsLayoutManager by lazy {
        FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
    }

    private val appBarBackground by lazy {
        combine(
            binding.powerMenuAppbar.scrollPercentage,
            binding.powerMenuContentRecyclerview.isScrolled,
            binding.powerMenuButtonsRecyclerview.adapter!!.changed.debounce(50)
        ) { scrollPercentage: Float, isScrolled: Boolean, _ ->
            val isMultiLine = buttonsLayoutManager.flexLines.size > 1
            if(isMultiLine){
                AppBarBackgroundState.Alpha(scrollPercentage)
            }else{
                if(isScrolled){
                    AppBarBackgroundState.AnimateToVisible
                }else{
                    AppBarBackgroundState.AnimateToInvisible
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        powerMenuApplication.loadOrReloadControlsModule(requireContext())
        view.setupBackground()
        setupButtonsRecyclerView()
        setupContentRecyclerView()
        setupButtons()
        setupService()
        setupAppbar()
        setupSecondaryAlpha()
        setupInsets(view)
    }

    private fun setupInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val cutout = insets.displayCutout
            view.updatePadding(left = cutout?.safeInsetLeft ?: 0, right = cutout?.safeInsetRight ?: 0)
            insets
        }
    }

    private fun View.setupBackground() {
        background = if(viewModel.useSolidBackground){
            ColorDrawable(
                monet.getBackgroundColorSecondary(context, true)
                    ?: monet.getBackgroundColor(context, true)
            )
        }else{
            ColorDrawable(
                ColorUtils.setAlphaComponent(
                    monet.getBackgroundColor(context, true),
                    128
                )
            )
        }
    }

    private fun setupButtonsRecyclerView() = with(binding.powerMenuButtonsRecyclerview) {
        layoutManager = buttonsLayoutManager
        adapter = buttonsAdapter
    }

    private fun setupContentRecyclerView() = with(binding.powerMenuContentRecyclerview) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = contentAdapter
        val bottomPadding = resources.getDimension(R.dimen.margin_16)
        ViewCompat.setOnApplyWindowInsetsListener(this){ view, insets ->
            val bottomNavInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val bottomCutoutInset = insets.displayCutout?.safeInsetBottom ?: 0
            view.updatePadding(bottom = bottomCutoutInset + bottomNavInset + bottomPadding.toInt())
            insets
        }
    }

    private fun setupService() = lifecycleScope.launchWhenResumed {
        serviceContainer.runWithService {
            setupWallet(it)
            setupControls()
        }
    }

    private var appBarBackgroundAnimation: ValueAnimator? = null
    private fun setupAppbar() = lifecycleScope.launchWhenResumed {
        //Start alpha is 0
        binding.powerMenuAppbar.background.alpha = 0
        binding.powerMenuAppbar.backgroundTintList = ColorStateList.valueOf(monet.getBackgroundColor(requireContext()))
        ViewCompat.setOnApplyWindowInsetsListener(binding.powerMenuAppbar){ view, insets ->
            val topInset = insets.displayCutout?.safeInsetTop ?: 0
            view.updatePadding(top = topInset)
            insets
        }
        launch {
            binding.powerMenuAppbar.awaitPost()
            binding.powerMenuAppbar.setExpanded(!viewModel.powerOptionsOpenCollapsed, false)
        }
        appBarBackground.collect {
            appBarBackgroundAnimation?.cancel()
            appBarBackgroundAnimation = null
            when(it){
                is AppBarBackgroundState.AnimateToVisible -> appBarBackgroundAnimation = binding.powerMenuAppbar.background.animateToVisible()
                is AppBarBackgroundState.AnimateToInvisible -> appBarBackgroundAnimation = binding.powerMenuAppbar.background.animateToInvisible()
                is AppBarBackgroundState.Alpha -> binding.powerMenuAppbar.background.alpha = ((it.alpha) * 255).safeRoundToInt()
            }
        }
    }

    private fun Float.safeRoundToInt(): Int {
        if(isNaN()) return 0
        return roundToInt()
    }

    private fun setupSecondaryAlpha() = lifecycleScope.launchWhenResumed {
        binding.powerMenuAppbar.scrollPercentage.collect {
            buttonsLayoutManager.setSecondaryAlpha(1 - it)
        }
    }

    private fun setupWallet(service: IClassicPowerMenu) {
        val quickAccessWalletClient =
            CPMQuickAccessWalletClientImpl(requireActivity().baseContext, service)
        contentAdapter.walletViewController = WalletPanelViewController(
            requireContext(),
            requireContext(),
            quickAccessWalletClient,
            this,
            keyguardManager.isDeviceLocked,
            WalletLoyaltyCardCallback(viewModel::addLoyaltyCardsToWallet)
        )
        contentAdapter.notifyItemChangeOfType(PowerMenuContentItem.Cards)
    }

    private fun setupControls() {
        contentAdapter.controlsUiController = controlsUiController
        contentAdapter.notifyItemChangeOfType(PowerMenuContentItem.Controls)
    }

    private fun setupButtons() = lifecycleScope.launchWhenResumed {
        buttonsAdapter.items = powerMenuButtons.filter { it.shouldShow() }.toMutableList()
        buttonsAdapter.notifyDataSetChanged()
    }

    override fun dismissGlobalActionsMenu() {
        sendDismiss()
    }

    private fun sendDismiss() = lifecycleScope.launchWhenResumed {
        serviceContainer.runWithService {
            it.sendDismissIntent(requireContext())
        }
    }

    private sealed class AppBarBackgroundState {
        data class Alpha(val alpha: Float): AppBarBackgroundState()
        object AnimateToInvisible: AppBarBackgroundState()
        object AnimateToVisible: AppBarBackgroundState()
    }



}
package com.kieronquinn.app.classicpowermenu.ui.screens.settings.container

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.navigation.AppNavigation
import com.kieronquinn.app.classicpowermenu.components.navigation.NavigationEvent
import com.kieronquinn.app.classicpowermenu.components.navigation.TransitionedActivityNavigator
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSettingsContainerBinding
import com.kieronquinn.app.classicpowermenu.ui.base.AutoExpandOnRotate
import com.kieronquinn.app.classicpowermenu.ui.base.BackAvailable
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.ui.base.ProvidesOverflow
import com.kieronquinn.app.classicpowermenu.utils.extensions.awaitPost
import com.kieronquinn.app.classicpowermenu.utils.extensions.isLandscape
import com.kieronquinn.app.classicpowermenu.utils.extensions.navigateSafely
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

class SettingsContainerFragment: BoundFragment<FragmentSettingsContainerBinding>(FragmentSettingsContainerBinding::inflate) {

    override val disableEnterExitAnimation = true

    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    private val googleSansMedium by lazy {
        ResourcesCompat.getFont(requireContext(), R.font.google_sans_text_medium)
    }

    private val navigation by inject<AppNavigation>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackground()
        setupInsets()
        setupToolbar()
        setupFragmentListener()
        setupNavigation()
        setupBack()
    }

    private fun setupBackground() {
        binding.root.setBackgroundColor(monet.getBackgroundColor(requireContext()))
    }

    private fun setupInsets(){
        binding.root.onApplyInsets { view, insets ->
            val navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(left = navigationInsets.left, right = navigationInsets.right)
        }
        binding.collapsingToolbar.onApplyInsets { view, insets ->
            view.updateLayoutParams<AppBarLayout.LayoutParams> {
                val appBarHeight = view.context.resources.getDimension(R.dimen.app_bar_height)
                height = (appBarHeight + insets.getInsets(WindowInsetsCompat.Type.statusBars()).top).roundToInt()
            }
        }
        binding.toolbar.onApplyInsets { view, insets ->
            getToolbarHeight()?.let {
                val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val overflowPadding = resources.getDimension(R.dimen.margin_8)
                val topInset = statusInsets.top
                view.updateLayoutParams<CollapsingToolbarLayout.LayoutParams> {
                    height = it + topInset
                }
                view.updatePadding(left = statusInsets.left, top = topInset, right = statusInsets.right + overflowPadding.toInt())
            }
        }
    }

    private fun getToolbarHeight(): Int? {
        val typedValue = TypedValue()
        return if (requireContext().theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
        }else null
    }

    private fun setupToolbar(){
        with(binding){
            collapsingToolbar.title = getString(R.string.app_name)
            appBar.setExpanded(!requireContext().isLandscape && getTopFragment() is AutoExpandOnRotate)
            toolbar.setNavigationOnClickListener {
                lifecycleScope.launchWhenResumed {
                    navigation.navigateBack()
                }
            }
            collapsingToolbar.setBackgroundColor(monet.getBackgroundColor(requireContext()))
            collapsingToolbar.setContentScrimColor(monet.getBackgroundColorSecondary(requireContext()) ?: monet.getBackgroundColor(requireContext()))
            collapsingToolbar.setExpandedTitleTypeface(googleSansMedium)
            collapsingToolbar.setCollapsedTitleTypeface(googleSansMedium)
        }
    }

    private fun setupNavigation() = viewLifecycleOwner.lifecycleScope.launchWhenResumed {
        navigation.navigationBus.collect {
            handleNavigationEvent(it)
        }
    }

    private fun setupBack() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!navController.navigateUp()) requireActivity().finish()
            }
        })
    }

    private fun getTopFragment(): Fragment? {
        if(!navHostFragment.isAdded) return null
        return navHostFragment.childFragmentManager.fragments.firstOrNull()
    }

    private fun setupFragmentListener(){
        navHostFragment.childFragmentManager.addOnBackStackChangedListener {
            getTopFragment()?.let {
                onTopFragmentChanged(it)
            }
        }
        lifecycleScope.launchWhenResumed {
            binding.navHostFragment.awaitPost()
            onTopFragmentChanged(getTopFragment() ?: return@launchWhenResumed)
        }
    }

    private fun onTopFragmentChanged(topFragment: Fragment){
        val backIcon = if(topFragment is BackAvailable){
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
        } else null
        if(topFragment is ProvidesOverflow){
            setupMenu(topFragment)
        }else{
            setupMenu(null)
        }
        binding.toolbar.navigationIcon = backIcon
        navController.currentDestination?.label?.let {
            if(it.isBlank()) return@let
            binding.collapsingToolbar.title = it
            binding.toolbar.title = it
        }
    }

    private fun setupMenu(menuProvider: ProvidesOverflow?){
        val menu = binding.toolbar.menu
        val menuInflater = MenuInflater(requireContext())
        menu.clear()
        menuProvider?.inflateMenu(menuInflater, menu)
        binding.toolbar.setOnMenuItemClickListener {
            menuProvider?.onMenuItemSelected(it) ?: false
        }
    }

    private fun handleNavigationEvent(event: NavigationEvent){
        when(event) {
            is NavigationEvent.Directions -> navController.navigateSafely(event.directions)
            is NavigationEvent.Id -> navController.navigateSafely(event.id)
            is NavigationEvent.PopupTo -> navController.popBackStack(event.id, event.popInclusive)
            is NavigationEvent.Back -> navController.navigateUp()
            is NavigationEvent.Intent -> startActivity(event.intent)
        }
    }

}
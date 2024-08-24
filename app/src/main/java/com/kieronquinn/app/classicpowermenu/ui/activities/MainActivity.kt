package com.kieronquinn.app.classicpowermenu.ui.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.service.accessibility.CPMAccessibilityService
import com.kieronquinn.app.classicpowermenu.utils.TransitionUtils
import com.kieronquinn.app.classicpowermenu.utils.extensions.broadcastReceiverAsFlow
import com.kieronquinn.app.classicpowermenu.utils.extensions.delayPreDrawUntilFlow
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenCreated
import com.kieronquinn.app.classicpowermenu.workers.UpdateCheckWorker
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : MonetCompatActivity() {

    private val viewModel by viewModel<MainActivityViewModel>()
    private val settings by inject<Settings>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupSplashScreen()
        }
        setupBringToFront()
        setupInsets()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        UpdateCheckWorker.queueCheckWorker(this)
        whenCreated {
            monet.awaitMonetReady()
            window.setBackgroundDrawable(ColorDrawable(monet.getBackgroundColor(this@MainActivity)))
            setContentView(R.layout.activity_main)
            setupStatusNav()
        }
        findViewById<View>(android.R.id.content).delayPreDrawUntilFlow(
            viewModel.appReady,
            lifecycle
        )
    }

    private fun setupInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView){ view, insets ->
            val cutout = insets.displayCutout
            view.updatePadding(left = cutout?.safeInsetLeft ?: 0, right = cutout?.safeInsetRight ?: 0)
            insets
        }
    }

    private fun setupStatusNav(){
        WindowInsetsControllerCompat(window, window.decorView).run {
            val lightStatusNav = resources.getBoolean(R.bool.lightStatusNav)
            isAppearanceLightStatusBars = lightStatusNav
            isAppearanceLightNavigationBars = lightStatusNav
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupSplashScreen() {
        //Use the default animation when not loading setup where there's a shared element
        if(!settings.hasSeenSetup) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.ALPHA,
                    1f,
                    0f
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 200L
                slideUp.doOnEnd { splashScreenView.remove() }
                slideUp.start()
            }
        }
    }

    private fun setupBringToFront() = whenCreated {
        broadcastReceiverAsFlow(CPMAccessibilityService.INTENT_ACTION_BRING_TO_FRONT, oneShot = true).collect {
            startActivity(Intent(this@MainActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            })
        }
    }

}
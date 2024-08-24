package com.kieronquinn.app.classicpowermenu.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.blur.BlurProvider
import com.kieronquinn.app.classicpowermenu.components.navigation.PowerMenuNavigation
import com.kieronquinn.app.classicpowermenu.components.starter.PowerMenuStarter
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.utils.extensions.awaitPost
import com.kieronquinn.app.classicpowermenu.utils.extensions.sendDismissIntent
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenCreated
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PowerMenuActivity : MonetCompatActivity(), PowerMenuStarter.PowerMenuStarterEventListener {

    private val blurProvider by inject<BlurProvider>()
    private val navigation by inject<PowerMenuNavigation>()
    private val viewModel by viewModel<PowerMenuActivityViewModel>()
    private val serviceContainer by inject<CPMServiceContainer>()
    private val starter by inject<PowerMenuStarter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = Fade()
        window.returnTransition = Fade()
        window.enterTransition = Fade()
        window.reenterTransition = Fade()
        window.setupWindowFlags()
        super.onCreate(savedInstanceState)
        requestedOrientation = viewModel.getRequestedOrientation()
        hideStatusBar()
        whenCreated {
            setContentView(R.layout.activity_power_menu)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setupLayout()
        }
        setupClose()
        sendDismiss {
            setupCloseBroadcast()
        }
    }

    private fun hideStatusBar(){
        WindowInsetsControllerCompat(window, window.decorView).run {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun sendDismiss(runAfter: () -> Unit) = whenResumed {
        serviceContainer.runWithService {
            it.sendDismissIntent(this@PowerMenuActivity)
            runAfter()
        }
    }

    override fun onResume() {
        super.onResume()
        starter.setEventListener(this)
        whenResumed {
            window.decorView.awaitPost()
            if(!viewModel.useSolidBackground){
                blurProvider.applyBlurToWindow(window, 1.5f)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        starter.setEventListener(null)
    }

    private fun setupClose() = whenCreated {
        navigation.closePowerMenuBus.collect {
            closePowerMenu()
        }
    }

    private fun setupCloseBroadcast() = whenCreated {
        viewModel.closeBroadcast.collect {
            closePowerMenu()
        }
    }

    private fun closePowerMenu(){
        finishAfterTransition()
    }

    private fun Window.setupWindowFlags(){
        addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
    }

    private fun Window.setupLayout(){
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        navigationBarColor = Color.TRANSPARENT
        statusBarColor = Color.TRANSPARENT
    }

    override fun onPowerButtonLongPressed(): Boolean {
        //Close the activity via Intent.ACTION_CLOSE_SYSTEM_DIALOGS
        viewModel.sendCloseBroadcast(this, false)
        //Don't re-fire event
        return false
    }

}
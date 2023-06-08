/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.controls.management

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.systemui.broadcast.BroadcastDispatcher
import com.android.systemui.controls.R
import com.android.systemui.controls.controller.ControlsController
import com.android.systemui.globalactions.GlobalActionsComponent
import com.android.systemui.settings.CurrentUserTracker
import com.android.systemui.util.LifecycleActivity
import com.android.systemui.util.MonetColorProvider
import com.android.systemui.util.extensions.createBackgroundExecutor
import com.android.systemui.util.extensions.createForgroundExecutor
import org.koin.android.ext.android.inject
import org.koin.core.component.inject
import java.util.concurrent.Executor

/**
 * Activity to select an application to favorite the [Control] provided by them.
 */
class ControlsProviderSelectorActivity : LifecycleActivity() {

    private val executor = createForgroundExecutor()
    private val backExecutor = createBackgroundExecutor()
    private val listingController by inject<ControlsListingController>()
    private val controlsController by inject<ControlsController>()
    private val globalActionsComponent by inject<GlobalActionsComponent>()
    private val broadcastDispatcher by inject<BroadcastDispatcher>()

    private val monet by inject<MonetColorProvider>()

    companion object {
        private const val TAG = "ControlsProviderSelectorActivity"
    }

    private lateinit var recyclerView: RecyclerView
    private val currentUserTracker = object : CurrentUserTracker(broadcastDispatcher) {
        private val startingUser = listingController.currentUserId

        override fun onUserSwitched(newUserId: Int) {
            if (newUserId != startingUser) {
                stopTracking()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.controls_management)

        lifecycle.addObserver(
            ControlsAnimations.observerForAnimations(
                requireViewById<ViewGroup>(R.id.controls_management_root),
                window,
                intent
            )
        )

        requireViewById<ViewStub>(R.id.stub).apply {
            layoutResource = R.layout.controls_management_apps
            inflate()
        }

        recyclerView = requireViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        requireViewById<TextView>(R.id.title).apply {
            text = resources.getText(R.string.controls_providers_title)
            monet.applyMonetToView(this, true)
        }

        requireViewById<Button>(R.id.other_apps).apply {
            monet.applyMonetToView(this, true)
            visibility = View.VISIBLE
            setText(android.R.string.cancel)
            setOnClickListener {
                onBackPressed()
            }
        }
        requireViewById<View>(R.id.done).visibility = View.GONE
    }

    override fun onBackPressed() {
        globalActionsComponent.handleShowGlobalActionsMenu()
        animateExitAndFinish()
    }

    override fun onStart() {
        super.onStart()
        currentUserTracker.startTracking()

        recyclerView.alpha = 0.0f
        recyclerView.adapter = AppAdapter(
                backExecutor,
                executor,
                lifecycle,
                listingController,
                LayoutInflater.from(this),
                ::launchFavoritingActivity,
                FavoritesRenderer(resources, controlsController::countFavoritesForComponent),
                resources).apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                var hasAnimated = false
                override fun onChanged() {
                    if (!hasAnimated) {
                        hasAnimated = true
                        ControlsAnimations.enterAnimation(recyclerView).start()
                    }
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        currentUserTracker.stopTracking()
    }

    /**
     * Launch the [ControlsFavoritingActivity] for the specified component.
     * @param component a component name for a [ControlsProviderService]
     */
    fun launchFavoritingActivity(component: ComponentName?) {
        executor.execute {
            component?.let {
                val intent = Intent(applicationContext, ControlsFavoritingActivity::class.java)
                        .apply {
                    putExtra(ControlsFavoritingActivity.EXTRA_APP,
                            listingController.getAppLabel(it))
                    putExtra(Intent.EXTRA_COMPONENT_NAME, it)
                    putExtra(ControlsFavoritingActivity.EXTRA_FROM_PROVIDER_SELECTOR, true)
                }
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
        }
    }

    override fun onDestroy() {
        currentUserTracker.stopTracking()
        super.onDestroy()
    }

    private fun animateExitAndFinish() {
        val rootView = requireViewById<ViewGroup>(R.id.controls_management_root)
        ControlsAnimations.exitAnimation(
                rootView,
                object : Runnable {
                    override fun run() {
                        finish()
                    }
                }
        ).start()
    }
}

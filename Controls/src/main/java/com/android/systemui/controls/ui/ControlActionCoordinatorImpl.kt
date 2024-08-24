/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.systemui.controls.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.VibrationEffect
import android.os.Vibrator
import android.service.controls.Control
import android.service.controls.actions.BooleanAction
import android.service.controls.actions.CommandAction
import android.service.controls.actions.FloatAction
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.annotation.MainThread
import com.android.systemui.controls.R
import com.android.systemui.globalactions.GlobalActionsComponent
import com.android.systemui.plugins.ActivityStarter
import com.android.systemui.statusbar.policy.KeyguardStateController
import com.android.systemui.util.concurrency.DelayableExecutor
import com.android.systemui.util.extensions.ServiceRunner
import org.koin.core.component.KoinComponent

class ControlActionCoordinatorImpl constructor(
    private val context: Context,
    private val bgExecutor: DelayableExecutor,
    private val uiExecutor: DelayableExecutor,
    private val activityStarter: ActivityStarter,
    private val keyguardStateController: KeyguardStateController,
    private val globalActionsComponent: GlobalActionsComponent,
    private val serviceRunner: ServiceRunner
) : ControlActionCoordinator {
    private var dialog: Dialog? = null
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private var pendingAction: Action? = null
    private var actionsInProgress = mutableSetOf<String>()

    companion object {
        private const val RESPONSE_TIMEOUT_IN_MILLIS = 3000L
    }

    override fun closeDialogs() {
        dialog?.dismiss()
        dialog = null
    }

    override fun toggle(cvh: ControlViewHolder, templateId: String, isChecked: Boolean) {
        bouncerOrRun(Action(cvh.cws.ci.controlId, {
            cvh.layout.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            cvh.action(BooleanAction(templateId, !isChecked))
        }, true /* blockable */))
    }

    override fun touch(cvh: ControlViewHolder, templateId: String, control: Control) {
        val blockable = cvh.usePanel()
        bouncerOrRun(Action(cvh.cws.ci.controlId, {
            cvh.layout.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            if (cvh.usePanel()) {
                serviceRunner.getIntentForPendingIntent(control.appIntent){
                    showDialog(cvh, it)
                }
            } else {
                cvh.action(CommandAction(templateId))
            }
        }, blockable))
    }

    override fun drag(isEdge: Boolean) {
        if (isEdge) {
            vibrate(Vibrations.rangeEdgeEffect)
        } else {
            vibrate(Vibrations.rangeMiddleEffect)
        }
    }

    override fun setValue(cvh: ControlViewHolder, templateId: String, newValue: Float) {
        bouncerOrRun(Action(cvh.cws.ci.controlId, {
            cvh.action(FloatAction(templateId, newValue))
        }, false /* blockable */))
    }

    override fun longPress(cvh: ControlViewHolder) {
        bouncerOrRun(Action(cvh.cws.ci.controlId, {
            // Long press snould only be called when there is valid control state, otherwise ignore
            cvh.cws.control?.let {
                cvh.layout.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                serviceRunner.getIntentForPendingIntent(it.appIntent){
                    showDialog(cvh, it)
                }
            }
        }, false /* blockable */))
    }

    override fun runPendingAction(controlId: String) {
        if (pendingAction?.controlId == controlId) {
            pendingAction?.invoke()
            pendingAction = null
        }
    }

    @MainThread
    override fun enableActionOnTouch(controlId: String) {
        actionsInProgress.remove(controlId)
    }

    private fun shouldRunAction(controlId: String) =
        if (actionsInProgress.add(controlId)) {
            uiExecutor.executeDelayed({
                actionsInProgress.remove(controlId)
            }, RESPONSE_TIMEOUT_IN_MILLIS)
            true
        } else {
            false
        }

    private fun bouncerOrRun(action: Action) {
        if (keyguardStateController.isShowing()) {
            var closeGlobalActions = !keyguardStateController.isUnlocked()
            if (closeGlobalActions) {
                activityStarter.dismissPowerMenu()

                // pending actions will only run after the control state has been refreshed
                pendingAction = action
            }

            activityStarter.dismissKeyguardThenExecute({
                Log.d(ControlsUiController.TAG, "Device unlocked, invoking controls action")
                if (closeGlobalActions) {
                    globalActionsComponent.handleShowGlobalActionsMenu()
                } else {
                    action.invoke()
                }
                true
            }, { pendingAction = null }, true /* afterKeyguardGone */)
        } else {
            action.invoke()
        }
    }

    private fun vibrate(effect: VibrationEffect) {
        bgExecutor.execute { vibrator.vibrate(effect) }
    }

    private fun showDialog(cvh: ControlViewHolder, intent: Intent) {
        bgExecutor.execute {
            val activities: List<ResolveInfo> = cvh.context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            uiExecutor.execute {
                // make sure the intent is valid before attempting to open the dialog
                if (activities.isNotEmpty()) {
                    /*dialog = DetailDialog(cvh, intent).also {
                        it.setOnDismissListener { _ -> dialog = null }
                        it.show()
                    }*/
                    activityStarter.dismissKeyguardThenExecute({
                        activityStarter.dismissPowerMenu()
                        serviceRunner.startActivity(context, intent.createLaunchIntent()){
                            serviceRunner.overridePendingTransition(R.anim.bottomsheet_in, R.anim.bottomsheet_out)
                        }
                        true
                    }, null, true)
                } else {
                    cvh.setErrorStatus()
                }
            }
        }
    }

    private fun Intent.createLaunchIntent(): Intent {
        val launchIntent = Intent(this)
        launchIntent.putExtra(DetailDialog.EXTRA_USE_PANEL, true)

        // Apply flags to make behaviour match documentLaunchMode=always.
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        return launchIntent
    }

    inner class Action(val controlId: String, val f: () -> Unit, val blockable: Boolean) {
        fun invoke() {
            if (!blockable || shouldRunAction(controlId)) {
                f.invoke()
            }
        }
    }
}

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

import android.app.ActivityView
import android.app.Dialog
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsets.Type
import android.view.WindowManager
import android.widget.ImageView

import com.android.systemui.controls.R

/**
 * A dialog that provides an {@link ActivityView}, allowing the application to provide
 * additional information and actions pertaining to a {@link android.service.controls.Control}.
 * The activity being launched is specified by {@link android.service.controls.Control#getAppIntent}.
 */
class DetailDialog(
    val cvh: ControlViewHolder,
    val intent: Intent
) : Dialog(cvh.context, R.style.Theme_SystemUI_Dialog_Control_DetailPanel) {

    companion object {
        private const val PANEL_TOP_OFFSET = "systemui.controls_panel_top_offset"
        /*
         * Indicate to the activity that it is being rendered in a bottomsheet, and they
         * should optimize the layout for a smaller space.
         */
        const val EXTRA_USE_PANEL = "controls.DISPLAY_IN_PANEL"
    }

    var activityView = ActivityView(context, null, 0, false)

    val stateCallback: ActivityView.StateCallback = object : ActivityView.StateCallback() {
        override fun onActivityViewReady(view: ActivityView) {
            val launchIntent = Intent(intent)
            launchIntent.putExtra(EXTRA_USE_PANEL, true)

            // Apply flags to make behaviour match documentLaunchMode=always.
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

            view.startActivity(launchIntent)
        }

        override fun onActivityViewDestroyed(view: ActivityView) {}

        override fun onTaskRemovalStarted(taskId: Int) {
            dismiss()
        }
    }

    init {
        //window?.setType(WindowManager_LayoutParams_TYPE_VOLUME_OVERLAY)
        setContentView(R.layout.controls_detail_dialog)

        requireViewById<ViewGroup>(R.id.controls_activity_view).apply {
            addView(activityView)
        }

        requireViewById<ImageView>(R.id.control_detail_close).apply {
            setOnClickListener { _: View -> dismiss() }
        }

        requireViewById<ImageView>(R.id.control_detail_open_in_app).apply {
            setOnClickListener { v: View ->
                dismiss()
                v.context.startActivity(intent)
            }
        }

        // consume all insets to achieve slide under effect
        window?.decorView?.setOnApplyWindowInsetsListener {
            _: View, insets: WindowInsets ->
                activityView.apply {
                    val l = getPaddingLeft()
                    val t = getPaddingTop()
                    val r = getPaddingRight()
                    setPadding(l, t, r, insets.getInsets(Type.systemBars()).bottom)
                }

                WindowInsets.CONSUMED
        }

        requireViewById<ViewGroup>(R.id.control_detail_root).apply {
            // use flag only temporarily for testing
            val resolver = cvh.context.contentResolver
            val defaultOffsetInPx = cvh.context.resources
                .getDimensionPixelSize(R.dimen.controls_activity_view_top_offset)
            val offsetInPx = Settings.Secure.getInt(resolver, PANEL_TOP_OFFSET, defaultOffsetInPx)

            val lp = getLayoutParams() as ViewGroup.MarginLayoutParams
            lp.topMargin = offsetInPx
            setLayoutParams(lp)

            setOnClickListener { dismiss() }
            (getParent() as View).setOnClickListener { dismiss() }
        }

        if (true){//TODO ScreenDecorationsUtils.supportsRoundedCornersOnWindows(context.getResources())) {
            val cornerRadius = context.resources
                .getDimensionPixelSize(R.dimen.controls_activity_view_corner_radius)
            activityView.setCornerRadius(cornerRadius.toFloat())
        }
    }

    override fun show() {
        activityView.setCallback(stateCallback)
        super.show()
    }

    override fun dismiss() {
        if (!isShowing()) return
        activityView.release()

        super.dismiss()
    }
}

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

import android.content.ComponentName
import android.service.controls.Control
import android.view.ViewGroup
import com.android.systemui.util.annotations.ResponseResult

interface ControlsUiController {
    val available: Boolean

    companion object {
        public const val TAG = "ControlsUiController"
        public const val EXTRA_ANIMATE = "extra_animate"
    }

    fun show(parent: ViewGroup, dismissGlobalActions: Runnable)
    fun hide()

    /**
     * Request all open dialogs be closed. Set [immediately] to true to dismiss without
     * animations.
     */
    fun closeDialogs(immediately: Boolean)

    fun onRefreshState(componentName: ComponentName, controls: List<Control>)
    fun onActionResponse(
        componentName: ComponentName,
        controlId: String,
        @ResponseResult response: Int
    )
}

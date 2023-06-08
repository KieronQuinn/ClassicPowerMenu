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

package com.android.systemui.util

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleOwner
import com.android.settingslib.core.lifecycle.Lifecycle
import org.koin.android.ext.android.inject

open class LifecycleActivity : Activity(), LifecycleOwner {

    private val _lifecycle = Lifecycle(this)
    private val monet by inject<MonetColorProvider>()

    override val lifecycle: Lifecycle
        get() = _lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        _lifecycle.onAttach(this)
        _lifecycle.onCreate(savedInstanceState)
        _lifecycle.handleLifecycleEvent(Event.ON_CREATE)
        super.onCreate(savedInstanceState)
        val backgroundColor = monet.getBackgroundColor(this, true)
        window.setBackgroundDrawable(ColorDrawable(backgroundColor))
        window.navigationBarColor = backgroundColor
    }

    override fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        _lifecycle.onAttach(this)
        _lifecycle.onCreate(savedInstanceState)
        _lifecycle.handleLifecycleEvent(Event.ON_CREATE)
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onStart() {
        _lifecycle.handleLifecycleEvent(Event.ON_START)
        super.onStart()
    }

    override fun onResume() {
        _lifecycle.handleLifecycleEvent(Event.ON_RESUME)
        super.onResume()
    }

    override fun onPause() {
        _lifecycle.handleLifecycleEvent(Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        _lifecycle.handleLifecycleEvent(Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroy() {
        _lifecycle.handleLifecycleEvent(Event.ON_DESTROY)
        super.onDestroy()
    }

}
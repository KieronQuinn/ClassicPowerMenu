package com.kieronquinn.app.classicpowermenu.utils

import android.app.Application
import android.util.Log
import androidx.lifecycle.*

/***
 * Copyright (c) 2017 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain	a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * Covered in detail in the book _Android's Architecture Components_
 * https://commonsware.com/AndroidArch
 */

abstract class LifecycleApplication : Application(), LifecycleObserver, LifecycleOwner {

    private val processLifecycleOwner by lazy {
        ProcessLifecycleOwner.get().lifecycle
    }

    override val lifecycle by lazy {
        processLifecycleOwner
    }

    override fun onCreate() {
        super.onCreate()
        processLifecycleOwner.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun created() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun started() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun resumed() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun paused() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun stopped() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyed() {}

}
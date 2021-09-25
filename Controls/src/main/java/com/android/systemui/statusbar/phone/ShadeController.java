/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.systemui.statusbar.phone;

import android.view.View;

/**
 * {@link ShadeController} is an abstraction of the work that used to be hard-coded in
 * {@link StatusBar}. The shade itself represents the concept of the status bar window state, and
 * can be in multiple states: dozing, locked, showing the bouncer, occluded, etc. All/some of these
 * are coordinated with {@link StatusBarKeyguardViewManager} via
 * {@link com.android.systemui.keyguard.KeyguardViewMediator} and others.
 */
public interface ShadeController {

    /**
     * If {@param animate}, does the same as {@link #collapsePanel()}. Otherwise, instantly collapse
     * the panel. Post collapse runnables will be executed
     *
     * @param animate
     */
    void collapsePanel(boolean animate);
}
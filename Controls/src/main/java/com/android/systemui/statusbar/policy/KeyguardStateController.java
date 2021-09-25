/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;

/**
 * Source of truth for keyguard state: If locked, occluded, has password, trusted etc.
 */
public interface KeyguardStateController extends CallbackController<Callback> {

    /**
     * If the device is locked or unlocked.
     */
    default boolean isUnlocked() {
        return !isShowing() || canDismissLockScreen();
    }

    /**
     * If the lock screen is visible.
     * The keyguard is also visible when the device is asleep or in always on mode, except when
     * the screen timed out and the user can unlock by quickly pressing power.
     *
     * This is unrelated to being locked or not.
     *
     * @see #isUnlocked()
     * @see #canDismissLockScreen()
     */
    boolean isShowing();

    /**
     * If swiping up will unlock without asking for a password.
     * @see #isUnlocked()
     */
    boolean canDismissLockScreen();

    interface Callback {

    }
}
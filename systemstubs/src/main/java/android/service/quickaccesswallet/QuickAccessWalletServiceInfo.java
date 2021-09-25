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

package android.service.quickaccesswallet;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * {@link ServiceInfo} and meta-data about a {@link android.service.quickaccesswallet.QuickAccessWalletService}.
 */
class QuickAccessWalletServiceInfo {

    @Nullable
    static QuickAccessWalletServiceInfo tryCreate(@NonNull Context context) {
        throw new RuntimeException("Stub!");
    }

    /**
     * @return the component name of the {@link android.service.quickaccesswallet.QuickAccessWalletService}
     */
    @NonNull
    ComponentName getComponentName() {
        throw new RuntimeException("Stub!");
    }

    /**
     * @return the fully qualified name of the activity that hosts the full wallet. If available,
     * this intent should be started with the action
     * {@link android.service.quickaccesswallet.QuickAccessWalletService#ACTION_VIEW_WALLET}
     */
    @Nullable
    String getWalletActivity() {
        throw new RuntimeException("Stub!");
    }

    /**
     * @return the fully qualified name of the activity that allows the user to change quick access
     * wallet settings. If available, this intent should be started with the action {@link
     * QuickAccessWalletService#ACTION_VIEW_WALLET_SETTINGS}
     */
    @Nullable
    String getSettingsActivity() {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    Drawable getWalletLogo(Context context) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    CharSequence getShortcutShortLabel(Context context) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    CharSequence getShortcutLongLabel(Context context) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    CharSequence getServiceLabel(Context context) {
        throw new RuntimeException("Stub!");
    }
}

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

package com.android.systemui.plugin.globalactions.wallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.PendingIntent;
import android.graphics.drawable.Drawable;

public interface WalletCardViewInfo {
    String getCardId();

    /**
     * Image of the card
     */
    @NonNull
    Drawable getCardDrawable();

    /**
     * Content description for the card
     */
    @Nullable
    CharSequence getContentDescription();

    /**
     * icon shown above card
     */
    @Nullable
    Drawable getIcon();

    /**
     * text shown above card
     */
    @NonNull
    CharSequence getText();

    @NonNull
    PendingIntent getPendingIntent();

    /**
     * Custom event handling for special cards
     * @return true if handled, false if it should be handled by the pending intent
     */
    default boolean onClick(){
        //Not handled by default
        return false;
    }

}

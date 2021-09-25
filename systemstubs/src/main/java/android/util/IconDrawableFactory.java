/*
 * Copyright (C) 2017 The Android Open Source Project
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
package android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;

/**
 * Utility class to load app drawables with appropriate badging.
 */
public class IconDrawableFactory {

    private IconDrawableFactory(Context context, boolean embedShadow) {
        throw new RuntimeException("Stub!");
    }

    protected boolean needsBadging(ApplicationInfo appInfo, int userId) {
        throw new RuntimeException("Stub!");
    }

    public Drawable getBadgedIcon(ApplicationInfo appInfo) {
        throw new RuntimeException("Stub!");
    }

    public Drawable getBadgedIcon(ApplicationInfo appInfo, int userId) {
        throw new RuntimeException("Stub!");
    }

    public Drawable getBadgedIcon(PackageItemInfo itemInfo, ApplicationInfo appInfo,
                                  int userId) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Add shadow to the icon if {@link AdaptiveIconDrawable}
     */
    public Drawable getShadowedIcon(Drawable icon) {
        throw new RuntimeException("Stub!");
    }

    public static IconDrawableFactory newInstance(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static IconDrawableFactory newInstance(Context context, boolean embedShadow) {
        throw new RuntimeException("Stub!");
    }
}
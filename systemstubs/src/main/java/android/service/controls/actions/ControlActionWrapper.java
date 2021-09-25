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
package android.service.controls.actions;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Wrapper for parceling/unparceling {@link ControlAction}.
 */
public final class ControlActionWrapper implements Parcelable {

    public ControlActionWrapper(@NonNull ControlAction controlAction) {
        throw new RuntimeException("Stub!");
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        throw new RuntimeException("Stub!");
    }
    @NonNull
    public ControlAction getWrappedAction() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }
    public static final @NonNull Creator<ControlActionWrapper> CREATOR =
            new Creator<ControlActionWrapper>() {
                @Override
                public ControlActionWrapper createFromParcel(@NonNull Parcel in) {
                    throw new RuntimeException("Stub!");
                }
                @Override
                public ControlActionWrapper[] newArray(int size) {
                    throw new RuntimeException("Stub!");
                }
            };
}
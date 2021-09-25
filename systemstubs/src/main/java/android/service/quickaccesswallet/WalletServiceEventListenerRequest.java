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

import androidx.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Register a dismiss request listener with the QuickAccessWalletService. This allows the service to
 * dismiss the wallet if it needs to show a payment activity in response to an NFC event.
 *
 * @hide
 */
public final class WalletServiceEventListenerRequest implements Parcelable {

    /**
     * Construct a new {@code DismissWalletListenerRequest}.
     *
     * @param listenerKey A unique key that identifies the listener.
     */
    public WalletServiceEventListenerRequest(@NonNull String listenerKey) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        throw new RuntimeException("Stub!");
    }

    private static WalletServiceEventListenerRequest readFromParcel(Parcel source) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public static final Creator<WalletServiceEventListenerRequest> CREATOR =
            new Creator<WalletServiceEventListenerRequest>() {
                @Override
                public WalletServiceEventListenerRequest createFromParcel(Parcel source) {
                    throw new RuntimeException("Stub!");
                }

                @Override
                public WalletServiceEventListenerRequest[] newArray(int size) {
                    throw new RuntimeException("Stub!");
                }
            };

    /**
     * Returns the unique key that identifies the wallet dismiss request listener.
     */
    @NonNull
    public String getListenerId() {
        throw new RuntimeException("Stub!");
    }
}

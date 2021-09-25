/*
 * Copyright 2020 The Android Open Source Project
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
 * Represents a request to a {@link QuickAccessWalletService} for {@link WalletCard walletCards}.
 * Wallet cards may represent anything that a user might carry in their wallet -- a credit card,
 * library card, a transit pass, etc. This request contains the desired size of the card images and
 * icons as well as the maximum number of cards that may be returned in the {@link
 * GetWalletCardsResponse}.
 *
 * <p>Cards may be displayed with an optional icon and label. The icon and label should communicate
 * the same idea. For example, if a card can be used at an NFC terminal, the icon could be an NFC
 * icon and the label could inform the user how to interact with the NFC terminal.
 *
 * <p>The maximum number of cards that may be displayed in the wallet is provided in {@link
 * #getMaxCards()}. The {@link QuickAccessWalletService} may provide up to this many cards in the
 * {@link GetWalletCardsResponse#getWalletCards()}. If the list of cards provided exceeds this
 * number, some of the cards may not be shown to the user.
 */
public final class GetWalletCardsRequest implements Parcelable {

    /**
     * Creates a new GetWalletCardsRequest.
     *
     * @param cardWidthPx  The width of the card image in pixels.
     * @param cardHeightPx The height of the card image in pixels.
     * @param iconSizePx   The width and height of the optional card icon in pixels.
     * @param maxCards     The maximum number of cards that may be provided in the response.
     */
    public GetWalletCardsRequest(int cardWidthPx, int cardHeightPx, int iconSizePx, int maxCards) {
        throw new RuntimeException("Stub!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        throw new RuntimeException("Stub!");
    }

    @NonNull
    public static final Creator<GetWalletCardsRequest> CREATOR =
            new Creator<GetWalletCardsRequest>() {
                @Override
                public GetWalletCardsRequest createFromParcel(Parcel source) {
                    throw new RuntimeException("Stub!");
                }

                @Override
                public GetWalletCardsRequest[] newArray(int size) {
                    throw new RuntimeException("Stub!");
                }
            };

    /**
     * The desired width of the {@link WalletCard#getCardImage()}, in pixels. The dimensions of the
     * card image are requested so that it may be rendered without scaling.
     * <p>
     * The {@code cardWidthPx} and {@code cardHeightPx} should be applied to the size of the {@link
     * WalletCard#getCardImage()}. The size of the card image is specified so that it may be
     * rendered accurately and without distortion caused by scaling.
     */
    public int getCardWidthPx() {
        throw new RuntimeException("Stub!");
    }

    /**
     * The desired height of the {@link WalletCard#getCardImage()}, in pixels. The dimensions of the
     * card image are requested so that it may be rendered without scaling.
     */
    public int getCardHeightPx() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Wallet cards may be displayed next to an icon. The icon can help to convey additional
     * information about the state of the card. If the provided icon is a bitmap, its width and
     * height should equal iconSizePx so that it is rendered without distortion caused by scaling.
     */
    public int getIconSizePx() {
        throw new RuntimeException("Stub!");
    }

    /**
     * The maximum size of the {@link GetWalletCardsResponse#getWalletCards()}. If the list of cards
     * exceeds this number, not all cards may be displayed.
     */
    public int getMaxCards() {
        throw new RuntimeException("Stub!");
    }
}

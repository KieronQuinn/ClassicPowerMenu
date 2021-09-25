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
import androidx.annotation.Nullable;
import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * A {@link WalletCard} can represent anything that a user might carry in their wallet -- a credit
 * card, library card, transit pass, etc. Cards are identified by a String identifier and contain a
 * card image, card image content description, and a {@link PendingIntent} to be used if the user
 * clicks on the card. Cards may be displayed with an icon and label, though these are optional.
 */
public final class WalletCard implements Parcelable {

    private WalletCard(Builder builder) {
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

    @NonNull
    public static final Creator<WalletCard> CREATOR =
            new Creator<WalletCard>() {
                @Override
                public WalletCard createFromParcel(Parcel source) {
                    throw new RuntimeException("Stub!");
                }

                @Override
                public WalletCard[] newArray(int size) {
                    throw new RuntimeException("Stub!");
                }
            };

    /**
     * The card id must be unique within the list of cards returned.
     */
    @NonNull
    public String getCardId() {
        throw new RuntimeException("Stub!");
    }

    /**
     * The visual representation of the card. If the card image Icon is a bitmap, it should have a
     * width of {@link android.service.quickaccesswallet.GetWalletCardsRequest#getCardWidthPx()} and a height of {@link
     * android.service.quickaccesswallet.GetWalletCardsRequest#getCardHeightPx()}.
     */
    @NonNull
    public Icon getCardImage() {
        throw new RuntimeException("Stub!");
    }

    /**
     * The content description of the card image.
     */
    @NonNull
    public CharSequence getContentDescription() {
        throw new RuntimeException("Stub!");
    }

    /**
     * If the user performs a click on the card, this PendingIntent will be sent. If the device is
     * locked, the wallet will first request device unlock before sending the pending intent.
     */
    @NonNull
    public PendingIntent getPendingIntent() {
        throw new RuntimeException("Stub!");
    }

    /**
     * An icon may be shown alongside the card image to convey information about how the card can be
     * used, or if some other action must be taken before using the card. For example, an NFC logo
     * could indicate that the card is NFC-enabled and will be provided to an NFC terminal if the
     * phone is held in close proximity to the NFC reader.
     *
     * <p>If the supplied Icon is backed by a bitmap, it should have width and height
     * {@link android.service.quickaccesswallet.GetWalletCardsRequest#getIconSizePx()}.
     */
    @Nullable
    public Icon getCardIcon() {
        throw new RuntimeException("Stub!");
    }

    /**
     * A card label may be shown alongside the card image to convey information about how the card
     * can be used, or if some other action must be taken before using the card. For example, an
     * NFC-enabled card could be labeled "Hold near reader" to inform the user of how to use NFC
     * cards when interacting with an NFC reader.
     *
     * <p>If the provided label is too long to fit on one line, it may be truncated and ellipsized.
     */
    @Nullable
    public CharSequence getCardLabel() {
        throw new RuntimeException("Stub!");
    }

    /**
     * Builder for {@link WalletCard} objects. You must to provide cardId, cardImage,
     * contentDescription, and pendingIntent. If the card is opaque and should be shown with
     * elevation, set hasShadow to true. cardIcon and cardLabel are optional.
     */
    public static final class Builder {

        /**
         * @param cardId             The card id must be non-null and unique within the list of
         *                           cards returned. <b>Note:
         *                           </b> this card ID should <b>not</b> contain PII (Personally
         *                           Identifiable Information, such as username or email address).
         * @param cardImage          The visual representation of the card. If the card image Icon
         *                           is a bitmap, it should have a width of {@link
         *                           android.service.quickaccesswallet.GetWalletCardsRequest#getCardWidthPx()} and a height of {@link
         *                           android.service.quickaccesswallet.GetWalletCardsRequest#getCardHeightPx()}. If the card image
         *                           does not have these dimensions, it may appear distorted when it
         *                           is scaled to fit these dimensions on screen. Bitmaps must be
         *                           of type {@link android.graphics.Bitmap.Config#HARDWARE} for
         *                           performance reasons.
         * @param contentDescription The content description of the card image. This field is
         *                           required and may not be null or empty.
         *                           <b>Note: </b> this message should <b>not</b> contain PII
         *                           (Personally Identifiable Information, such as username or email
         *                           address).
         * @param pendingIntent      If the user performs a click on the card, this PendingIntent
         *                           will be sent. If the device is locked, the wallet will first
         *                           request device unlock before sending the pending intent. It is
         *                           recommended that the pending intent be immutable (use {@link
         *                           PendingIntent#FLAG_IMMUTABLE}).
         */
        public Builder(@NonNull String cardId,
                @NonNull Icon cardImage,
                @NonNull CharSequence contentDescription,
                @NonNull PendingIntent pendingIntent) {
            throw new RuntimeException("Stub!");
        }

        /**
         * An icon may be shown alongside the card image to convey information about how the card
         * can be used, or if some other action must be taken before using the card. For example, an
         * NFC logo could indicate that the card is NFC-enabled and will be provided to an NFC
         * terminal if the phone is held in close proximity to the NFC reader. This field is
         * optional.
         *
         * <p>If the supplied Icon is backed by a bitmap, it should have width and height
         * {@link GetWalletCardsRequest#getIconSizePx()}.
         */
        @NonNull
        public Builder setCardIcon(@Nullable Icon cardIcon) {
            throw new RuntimeException("Stub!");
        }

        /**
         * A card label may be shown alongside the card image to convey information about how the
         * card can be used, or if some other action must be taken before using the card. For
         * example, an NFC-enabled card could be labeled "Hold near reader" to inform the user of
         * how to use NFC cards when interacting with an NFC reader. This field is optional.
         * <b>Note: </b> this card label should <b>not</b> contain PII (Personally Identifiable
         * Information, such as username or email address). If the provided label is too long to fit
         * on one line, it may be truncated and ellipsized.
         */
        @NonNull
        public Builder setCardLabel(@Nullable CharSequence cardLabel) {
            throw new RuntimeException("Stub!");
        }

        /**
         * Builds a new {@link WalletCard} instance.
         *
         * @return A built response.
         */
        @NonNull
        public WalletCard build() {
            throw new RuntimeException("Stub!");
        }
    }
}

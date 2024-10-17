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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.Looper;
import android.service.quickaccesswallet.GetWalletCardsError;
import android.service.quickaccesswallet.GetWalletCardsRequest;
import android.service.quickaccesswallet.GetWalletCardsResponse;
import android.service.quickaccesswallet.QuickAccessWalletClient;
import android.service.quickaccesswallet.SelectWalletCardRequest;
import android.service.quickaccesswallet.WalletCard;
import android.service.quickaccesswallet.WalletServiceEvent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.android.systemui.plugin.globalactions.wallet.WalletPopupMenu.OverflowItem;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.plugins.activitystarter.WalletActivityStarter;
import com.android.systemui.plugins.activitystarter.WalletActivityStarterProvider;
import com.android.systemui.plugins.loyaltycards.WalletLoyaltyCardCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import kotlin.Unit;
import kotlin.reflect.KFunction;

public class WalletPanelViewController implements
        GlobalActionsPanelPlugin.PanelViewController,
        WalletCardCarousel.OnSelectionListener,
        QuickAccessWalletClient.OnWalletCardsRetrievedCallback,
        QuickAccessWalletClient.WalletServiceEventListener {

    private static final String TAG = "WalletPanelViewCtrl";
    private static final int MAX_CARDS = 10;
    private static final long SELECTION_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30);
    private static final String PREFS_WALLET_VIEW_HEIGHT = "wallet_view_height";
    private static final String PREFS_HAS_CARDS = "has_cards";
    private static final String SETTINGS_PKG = "com.android.settings";
    private static final String SETTINGS_ACTION = SETTINGS_PKG + ".GLOBAL_ACTIONS_PANEL_SETTINGS";
    private final Context mSysuiContext;
    private final Context mPluginContext;
    private final QuickAccessWalletClient mWalletClient;
    private final WalletView mWalletView;
    private final WalletCardCarousel mWalletCardCarousel;
    private final GlobalActionsPanelPlugin.Callbacks mPluginCallbacks;
    private final ExecutorService mExecutor;
    private final Handler mHandler;
    private final Runnable mSelectionRunnable = this::selectCard;
    private final SharedPreferences mPrefs;
    private boolean mIsDeviceLocked;
    private boolean mIsDismissed;
    private boolean mHasRegisteredListener;
    private String mDefaultCardId;
    private String mSelectedCardId;
    private final WalletLoyaltyCardCallback mWalletLoyaltyCardCallback;

    private final WalletActivityStarter activityStarter = WalletActivityStarterProvider.INSTANCE.getActivityStarter();

    public WalletPanelViewController(
            Context sysuiContext,
            Context pluginContext,
            QuickAccessWalletClient walletClient,
            GlobalActionsPanelPlugin.Callbacks pluginCallbacks,
            boolean isDeviceLocked,
            WalletLoyaltyCardCallback walletLoyaltyCardCallback) {
        mSysuiContext = sysuiContext;
        mPluginContext = pluginContext;
        mWalletClient = walletClient;
        mPrefs = mSysuiContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mPluginCallbacks = pluginCallbacks;
        mIsDeviceLocked = isDeviceLocked;
        mWalletLoyaltyCardCallback = walletLoyaltyCardCallback;
        mWalletView = new WalletView(pluginContext);
        mWalletView.setClipChildren(false);
        mWalletView.setMinimumHeight(getExpectedMinHeight());
        mWalletView.setLayoutParams(
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT));
        mWalletCardCarousel = mWalletView.getCardCarousel();
        mWalletCardCarousel.setSelectionListener(this);
        mHandler = new Handler(Looper.myLooper());
        mExecutor = Executors.newSingleThreadExecutor();
        if (!mPrefs.getBoolean(PREFS_HAS_CARDS, false)) {
            // The empty state view is shown preemptively when cards were not returned last time
            // to decrease perceived latency.
            showEmptyStateView();
        }
    }

    /**
     * Implements {@link GlobalActionsPanelPlugin.PanelViewController}. Returns the {@link View}
     * containing the Quick Access Wallet.
     */
    @Override
    public View getPanelContent() {
        return mWalletView;
    }

    /**
     * Implements {@link GlobalActionsPanelPlugin.PanelViewController}. Invoked when the view
     * containing the Quick Access Wallet is dismissed.
     */
    @Override
    public void onDismissed() {
        if (mIsDismissed) {
            return;
        }
        mIsDismissed = true;
        mSelectedCardId = null;
        mHandler.removeCallbacks(mSelectionRunnable);
        mWalletClient.notifyWalletDismissed();
        mWalletClient.removeWalletServiceEventListener(this);
        mWalletView.animateDismissal();
    }

    /**
     * Implements {@link GlobalActionsPanelPlugin.PanelViewController}. Invoked when the device is
     * either locked or unlocked while the wallet is visible.
     */
    @Override
    public void onDeviceLockStateChanged(boolean deviceLocked) {
        if (mIsDismissed || mIsDeviceLocked == deviceLocked || !mIsDeviceLocked) {
            // Disregard repeat events and events after unlock
            return;
        }
        mIsDeviceLocked = deviceLocked;
        // Cards are re-queried because the wallet application may wish to change card art, icons,
        // text, or other attributes depending on the lock state of the device.
        queryWalletCards();
    }

    /**
     * Query wallet cards from the client and display them on screen.
     */
    public void queryWalletCards() {
        if (mIsDismissed) {
            return;
        }
        if (!mHasRegisteredListener) {
            // Listener is registered even when device is locked. Should only be registered once.
            mWalletClient.addWalletServiceEventListener(this);
            mHasRegisteredListener = true;
        }
        if (mIsDeviceLocked && !mWalletClient.isWalletFeatureAvailableWhenDeviceLocked()) {
            mWalletView.hide();
            return;
        }
        mWalletView.show();
        mWalletView.hideErrorMessage();
        int cardWidthPx = mWalletCardCarousel.getCardWidthPx();
        int cardHeightPx = mWalletCardCarousel.getCardHeightPx();
        int iconSizePx = mWalletView.getIconSizePx();
        GetWalletCardsRequest request =
                new GetWalletCardsRequest(cardWidthPx, cardHeightPx, iconSizePx, MAX_CARDS);
        mWalletClient.getWalletCards(mExecutor, request, this);
    }

    /**
     * Implements {@link QuickAccessWalletClient.OnWalletCardsRetrievedCallback}. Called when cards
     * are retrieved successfully from the service. This is called on {@link #mExecutor}.
     */
    @Override
    public void onWalletCardsRetrieved(GetWalletCardsResponse response) {
        if (mIsDismissed) {
            return;
        }
        List<WalletCard> walletCards = response.getWalletCards();

        walletCards.removeIf(new Predicate<WalletCard>() {
            @Override
            public boolean test(WalletCard walletCard) {
                return walletCard.getCardId().length() <= 1;
            }
        });

        ArrayList<WalletCardViewInfo> data = new ArrayList<>(walletCards.size());
        for (WalletCard card : walletCards) {
            data.add(new QAWalletCardViewInfo(card));
        }

        if(walletCards.isEmpty()){
            mDefaultCardId = null;
        }else {
            mDefaultCardId = data.get(response.getSelectedIndex()).getCardId();
        }

        mWalletLoyaltyCardCallback.getMethod().invoke(data, () -> {
            if(data.isEmpty()) return;
            //Make sure we're still attached
            if(mWalletView.isAttachedToWindow()) {
                setupWalletView(data, response);
            }
        });
    }

    private void setupWalletView(List<WalletCardViewInfo> data, GetWalletCardsResponse response){
        // Get on main thread for UI updates
        mWalletView.post(() -> {
            if (mIsDismissed) {
                return;
            }
            if (data.isEmpty()) {
                showEmptyStateView();
            } else {
                mWalletView.showCardCarousel(data, response.getSelectedIndex(), getOverflowItems());
            }
            // The empty state view will not be shown preemptively next time if cards were returned
            mPrefs.edit().putBoolean(PREFS_HAS_CARDS, !data.isEmpty()).apply();
            removeMinHeightAndRecordHeightOnLayout();
        });
    }

    /**
     * Implements {@link QuickAccessWalletClient.OnWalletCardsRetrievedCallback}. Called when there
     * is an error during card retrieval. This will be run on the {@link #mExecutor}.
     */
    @Override
    public void onWalletCardRetrievalError(GetWalletCardsError error) {
        mWalletView.post(() -> {
            if (mIsDismissed) {
                return;
            }
            mWalletView.showErrorMessage(error.getMessage());
        });
    }

    /**
     * Implements {@link QuickAccessWalletClient.WalletServiceEventListener}. Called when the wallet
     * application propagates an event, such as an NFC tap, to the quick access wallet view.
     */
    @Override
    public void onWalletServiceEvent(WalletServiceEvent event) {
        if (mIsDismissed) {
            return;
        }
        switch (event.getEventType()) {
            case WalletServiceEvent.TYPE_NFC_PAYMENT_STARTED:
                mPluginCallbacks.dismissGlobalActionsMenu();
                onDismissed();
                break;
            case 2: //TYPE_WALLET_CARDS_UPDATED = 2
                queryWalletCards();
                break;
            default:
                Log.w(TAG, "onWalletServiceEvent: Unknown event type");
        }
    }

    /**
     * Implements {@link WalletCardCarousel.OnSelectionListener}. Called when the user selects a
     * card from the carousel by scrolling to it.
     */
    @Override
    public void onCardSelected(WalletCardViewInfo card) {
        if (mIsDismissed) {
            return;
        }
        mSelectedCardId = card.getCardId();
        selectCard();
    }

    private void selectCard() {
        mHandler.removeCallbacks(mSelectionRunnable);
        String selectedCardId = mSelectedCardId;
        if (mIsDismissed || selectedCardId == null) {
            return;
        }
        //Don't send a selection event for a loyalty card
        if(!mSelectedCardId.startsWith("LOY")) {
            mWalletClient.selectWalletCard(new SelectWalletCardRequest(selectedCardId));
        }else if(mDefaultCardId != null){
            mWalletClient.selectWalletCard(new SelectWalletCardRequest(mDefaultCardId));
        }
        // Re-selecting the card keeps the connection bound so we continue to get service events
        // even if the user keeps it open for a long time.
        mHandler.postDelayed(mSelectionRunnable, SELECTION_DELAY_MILLIS);
    }

    /**
     * Implements {@link WalletCardCarousel.OnSelectionListener}. Called when the user clicks on a
     * card.
     */
    @Override
    public void onCardClicked(WalletCardViewInfo card) {
        if (mIsDismissed) {
            return;
        }
        if(!card.onClick()){
            activityStarter.runAfterKeyguardDismissed(() -> {
                PendingIntent pendingIntent = card.getPendingIntent();
                startPendingIntent(pendingIntent);
            });
        }
    }

    private OverflowItem[] getOverflowItems() {
        CharSequence walletLabel = mWalletClient.getShortcutShortLabel();
        Intent walletIntent = mWalletClient.createWalletIntent();
        CharSequence settingsLabel = mPluginContext.getString(R.string.settings);
        Intent settingsIntent = new Intent(SETTINGS_ACTION).setPackage(SETTINGS_PKG);
        OverflowItem settings = new OverflowItem(settingsLabel, () -> startIntent(settingsIntent));
        if (!TextUtils.isEmpty(walletLabel) && walletIntent != null) {
            OverflowItem wallet = new OverflowItem(walletLabel, () -> startIntent(walletIntent));
            return new OverflowItem[]{wallet };//, settings};
        } else {
            return new OverflowItem[]{};//settings};
        }
    }

    private void showEmptyStateView() {
        Drawable logo = mWalletClient.getLogo();
        CharSequence logoContentDesc = mWalletClient.getServiceLabel();
        CharSequence label = mWalletClient.getShortcutLongLabel();
        Intent intent = mWalletClient.createWalletIntent();
        if (logo == null
                || TextUtils.isEmpty(logoContentDesc)
                || TextUtils.isEmpty(label)
                || intent == null) {
            Log.w(TAG, "QuickAccessWalletService manifest entry mis-configured");
            // Issue is not likely to be resolved until manifest entries are enabled.
            // Hide wallet feature until then.
            mWalletView.hide();
            mPrefs.edit().putInt(PREFS_WALLET_VIEW_HEIGHT, 0).apply();
        } else {
            mWalletView.showEmptyStateView(logo, logoContentDesc, label, v -> startIntent(intent));
        }
    }

    private void startIntent(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mSysuiContext, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        startPendingIntent(pendingIntent);
    }

    private void startPendingIntent(PendingIntent pendingIntent) {
        mPluginCallbacks.startPendingIntentDismissingKeyguard(pendingIntent);
        mPluginCallbacks.dismissGlobalActionsMenu();
        onDismissed();
    }

    /**
     * The total view height depends on whether cards are shown or not. Since it is not known at
     * construction time whether cards will be available, the best we can do is set the height to
     * whatever it was the last time. Setting the height correctly ahead of time is important
     * because Home Controls are shown below the wallet and may be displayed before card data is
     * loaded, causing the home controls to jump down when card data arrives.
     */
    private int getExpectedMinHeight() {
        int expectedHeight = mPrefs.getInt(PREFS_WALLET_VIEW_HEIGHT, -1);
        if (expectedHeight == -1) {
            Resources res = mPluginContext.getResources();
            expectedHeight = res.getDimensionPixelSize(R.dimen.min_wallet_empty_height);
        }
        return expectedHeight;
    }

    private void removeMinHeightAndRecordHeightOnLayout() {
        mWalletView.setMinimumHeight(0);
        mWalletView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                    int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mWalletView.removeOnLayoutChangeListener(this);
                mPrefs.edit().putInt(PREFS_WALLET_VIEW_HEIGHT, bottom - top).apply();
            }
        });
    }

    private class QAWalletCardViewInfo implements WalletCardViewInfo {

        private final WalletCard mWalletCard;
        private final Drawable mCardDrawable;
        private final Drawable mIconDrawable;

        /**
         * Constructor is called on background executor, so it is safe to load drawables
         * synchronously.
         */
        QAWalletCardViewInfo(WalletCard walletCard) {
            mWalletCard = walletCard;
            mCardDrawable = mWalletCard.getCardImage().loadDrawable(mPluginContext);
            Icon icon = mWalletCard.getCardIcon();
            mIconDrawable = icon == null ? null : icon.loadDrawable(mPluginContext);
        }

        @Override
        public String getCardId() {
            return mWalletCard.getCardId();
        }

        @Override
        public Drawable getCardDrawable() {
            return mCardDrawable;
        }

        @Override
        public CharSequence getContentDescription() {
            return mWalletCard.getContentDescription();
        }

        @Override
        public Drawable getIcon() {
            return mIconDrawable;
        }

        @Override
        public CharSequence getText() {
            return mWalletCard.getCardLabel();
        }

        @Override
        public PendingIntent getPendingIntent() {
            return mWalletCard.getPendingIntent();
        }
    }
}

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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;

import com.android.systemui.plugins.monet.MonetColorProvider;
import com.android.systemui.plugins.monet.MonetColorProviderProvider;

/**
 * This is the overflow popup menu. To maintain visual consistency within Global Actions, this
 * implementation and {@code com.android.systemui.globalactions.GlobalActionsPopupMenu} should stay
 * in sync.
 */
class WalletPopupMenu extends ListPopupWindow {

    private final Context mContext;
    private final ArrayAdapter<OverflowItem> mAdapter;
    private final MonetColorProvider monet = MonetColorProviderProvider.INSTANCE.getMonetColorProvider();

    WalletPopupMenu(Context context, View anchorView) {
        super(new ContextThemeWrapper(context, R.style.Wallet_ListPopupWindow));
        mContext = context;
        //setWindowLayoutType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY);
        setInputMethodMode(INPUT_METHOD_NOT_NEEDED);
        setModal(true);
        setAnchorView(anchorView);
        setDropDownGravity(Gravity.END);
        mAdapter = new ArrayAdapter<>(context, R.layout.wallet_more_item);
        setAdapter(mAdapter);
        Resources res = context.getResources();
        Drawable background = res.getDrawable(R.drawable.wallet_overflow_popup_bg, null);
        background.setTint(monet.getBackgroundColorSecondary(context, true));
        setBackgroundDrawable(background);
        setVerticalOffset(res.getDimensionPixelSize(R.dimen.wallet_menu_vertical_offset));
        int horizontalOffset = res.getDimensionPixelSize(R.dimen.wallet_menu_horizontal_offset);
        boolean isLtr = res.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
        setHorizontalOffset(isLtr ? -horizontalOffset : horizontalOffset);
        setOnItemClickListener((parent, view, position, id) -> {
            mAdapter.getItem(position).onClickListener.run();
            dismiss();
        });
    }

    void setMenuItems(OverflowItem[] menuItems) {
        mAdapter.clear();
        mAdapter.addAll(menuItems);
        setContentWidth(measureContentWidth());
    }

    private int measureContentWidth() {
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        // width should be between [.5, .9] of screen
        int width = Math.round(screenWidth * 0.5f);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int count = mAdapter.getCount();
        FrameLayout measureParent = new FrameLayout(mContext);
        View itemView = null;
        for (int i = 0; i < count; i++) {
            itemView = mAdapter.getView(i, itemView, measureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(itemView.getMeasuredWidth(), width);
        }
        int maxWidth = Math.round(screenWidth * 0.9f);
        return Math.min(maxWidth, width);
    }

    static class OverflowItem {
        final CharSequence label;
        final Runnable onClickListener;

        OverflowItem(CharSequence label, Runnable onClickListener) {
            this.label = label;
            this.onClickListener = onClickListener;
        }

        @Override
        public String toString() {
            return label.toString();
        }
    }
}

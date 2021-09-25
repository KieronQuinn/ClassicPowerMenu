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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

/**
 * A CardView that draws a light border around its contents. This is done to make black cards easier
 * to see against the black background of the global actions menu. On more brightly colored cards,
 * the border is barely visible.
 */
public class WalletCardView extends CardView {
    private final Paint mBorderPaint;

    public WalletCardView(@NonNull Context context) {
        this(context, null);
    }

    public WalletCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBorderPaint = new Paint();
        mBorderPaint.setColor(context.getColor(R.color.wallet_card_border));
        mBorderPaint.setStrokeWidth(
                context.getResources().getDimension(R.dimen.wallet_card_border_width));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        float radius = getRadius();
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius, mBorderPaint);
    }
}

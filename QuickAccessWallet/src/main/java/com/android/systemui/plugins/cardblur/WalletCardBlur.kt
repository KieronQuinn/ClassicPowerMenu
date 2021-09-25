package com.android.systemui.plugins.cardblur

import android.graphics.drawable.Drawable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface WalletCardBlurProvider {

    fun applyBlurToCardDrawableIfNeeded(cardDrawable: Drawable, cardType: String): Drawable

}

object WalletCardBlur: KoinComponent {

    private val provider by inject<WalletCardBlurProvider>()

    fun getWalletCardBlurProvider(): WalletCardBlurProvider {
        return provider
    }

}
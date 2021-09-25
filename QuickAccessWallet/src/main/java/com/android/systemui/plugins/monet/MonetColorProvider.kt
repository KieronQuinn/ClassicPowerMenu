package com.android.systemui.plugins.monet

import android.content.Context
import androidx.annotation.ColorInt
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface MonetColorProvider {

    @ColorInt
    fun getPrimaryColor(context: Context, forceDarkMode: Boolean): Int

    @ColorInt
    fun getAccentColor(context: Context, forceDarkMode: Boolean): Int

    @ColorInt
    fun getBackgroundColor(context: Context, forceDarkMode: Boolean): Int

    @ColorInt
    fun getBackgroundColorSecondary(context: Context, forceDarkMode: Boolean): Int

}

object MonetColorProviderProvider: KoinComponent {

    private val provider by inject<MonetColorProvider>()

    fun getMonetColorProvider(): MonetColorProvider {
        return provider
    }

}
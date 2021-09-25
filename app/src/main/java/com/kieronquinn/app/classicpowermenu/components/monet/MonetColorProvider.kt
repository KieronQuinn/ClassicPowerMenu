package com.kieronquinn.app.classicpowermenu.components.monet

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import com.kieronquinn.monetcompat.extensions.views.applyMonetRecursively
import com.kieronquinn.monetcompat.extensions.views.overrideRippleColor
import com.kieronquinn.monetcompat.extensions.views.setTint
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.android.systemui.plugins.monet.MonetColorProvider as WalletMonetColorProvider
import com.android.systemui.util.MonetColorProvider as ControlsMonetColorProvider

class MonetColorProvider(context: Context): ControlsMonetColorProvider, WalletMonetColorProvider, KoinComponent {

    private val settings by inject<Settings>()

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    private val googleSansText by lazy {
        ResourcesCompat.getFont(context, R.font.google_sans_text)
    }

    private val googleSansTextMedium by lazy {
        ResourcesCompat.getFont(context, R.font.google_sans_text_medium)
    }

    override fun getPrimaryColor(context: Context, forceDarkMode: Boolean): Int {
        //This is only used in the Controls tiles so shouldn't be the app primary color
        if(!settings.useMonet) return ContextCompat.getColor(context, R.color.background_dark)
        return monet.getPrimaryColor(context, forceDarkMode)
    }

    override fun getAccentColor(context: Context, forceDarkMode: Boolean): Int {
        if(!settings.useMonet) return ContextCompat.getColor(context, R.color.accent)
        return monet.getAccentColor(context, forceDarkMode)
    }

    override fun getBackgroundColor(context: Context, forceDarkMode: Boolean): Int {
        if(!settings.useMonet) return ContextCompat.getColor(context, R.color.background)
        return monet.getBackgroundColor(context, forceDarkMode)
    }

    override fun getBackgroundColorSecondary(context: Context, forceDarkMode: Boolean): Int {
        if(!settings.useMonet) return ContextCompat.getColor(context, if(forceDarkMode) R.color.background_dark else R.color.background_secondary)
        return monet.getBackgroundColorSecondary(context, forceDarkMode) ?: monet.getBackgroundColor(context, forceDarkMode)
    }

    override fun applyMonetToView(view: View, forceDarkMode: Boolean) {
        when(view){
            is CheckBox -> view.setTint(monet.getAccentColor(view.context, forceDarkMode))
            is Button -> {
                view.typeface = googleSansTextMedium
                view.isAllCaps = false
                view.setTextColor(monet.getAccentColor(view.context, forceDarkMode))
                view.overrideRippleColor(monet.getAccentColor(view.context, forceDarkMode))
            }
            is TextView -> {
                if(view.typeface?.weight == 500) {
                    view.typeface = googleSansTextMedium
                }else{
                    view.typeface = googleSansText
                }
            }
        }
    }

}
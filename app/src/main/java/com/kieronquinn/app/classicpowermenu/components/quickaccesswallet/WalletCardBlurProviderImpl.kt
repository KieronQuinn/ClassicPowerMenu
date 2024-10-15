package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet

import android.app.KeyguardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.android.systemui.plugins.cardblur.WalletCardBlurProvider
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import kotlin.math.roundToInt


class WalletCardBlurProviderImpl(context: Context, private val settings: Settings): WalletCardBlurProvider {

    private val keyguardManager by lazy {
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }

    private val renderScript by lazy {
        RenderScript.create(context)
    }

    private val resources = context.resources

    /**
     *  Position of the card number on the cards produced by Google Pay, in relation to the total
     *  width/height
     */
    companion object {
        private const val CARD_Y_TOP = 0.816239316f
        private const val CARD_Y_BOTTOM = 0.876068376f
        private const val CARD_X_START = 0.148993289f
        private const val CARD_X_END = 0.263087248f
    }

    override fun applyBlurToCardDrawableIfNeeded(cardDrawable: Drawable, cardType: String): Drawable {
        if(cardDrawable !is BitmapDrawable) return cardDrawable
        //Only blur cards with a visible number
        if(cardType != "2") return cardDrawable
        //Don't blur if not locked
        if(!keyguardManager.isDeviceLocked && !settings.developerContentCreatorMode) return cardDrawable
        if(!settings.quickAccessWalletHideCardNumberWhenLocked && !settings.developerContentCreatorMode) return cardDrawable
        val cardBitmap = cardDrawable.bitmap.config?.let { cardDrawable.bitmap.copy(it, true) }
            ?: return cardDrawable
        //Calculate the position of the last 4 digits of the card number
        val cardYTop = (CARD_Y_TOP * cardBitmap.height).roundToInt()
        val cardYBottom = (CARD_Y_BOTTOM * cardBitmap.height).roundToInt()
        val cardXStart = (CARD_X_START * cardBitmap.width).roundToInt()
        val cardXEnd = (CARD_X_END * cardBitmap.width).roundToInt()
        //Create a cropped bitmap of just the last 4 digits
        val outWidth = cardXEnd - cardXStart
        val outHeight = cardYBottom - cardYTop
        val cropped = Bitmap.createBitmap(cardBitmap, cardXStart, cardYTop, outWidth, outHeight).run {
            //Blur the bitmap
            blurBitmap(this, 20f)
        }
        //Put the blurred bitmap back on top of the original image in the same position
        Canvas(cardBitmap).run {
            drawBitmap(cropped, cardXStart.toFloat(), cardYTop.toFloat(), Paint())
        }
        return BitmapDrawable(resources, cardBitmap)
    }

    private fun blurBitmap(source: Bitmap, radius: Float): Bitmap {
        val bitmap = source.config?.let { source.copy(it, true) } ?: return source
        val input = Allocation.createFromBitmap(
            renderScript,
            source,
            Allocation.MipmapControl.MIPMAP_NONE,
            Allocation.USAGE_SCRIPT
        )
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        return bitmap
    }

}
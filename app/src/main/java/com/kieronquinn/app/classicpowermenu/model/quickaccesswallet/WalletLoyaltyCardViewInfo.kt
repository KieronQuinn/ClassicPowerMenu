package com.kieronquinn.app.classicpowermenu.model.quickaccesswallet

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import com.android.systemui.plugin.globalactions.wallet.WalletCardViewInfo
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.GooglePayConstants
import com.kieronquinn.app.classicpowermenu.utils.extensions.drawCenteredText
import com.kieronquinn.app.classicpowermenu.utils.extensions.getRoundedBitmap

/**
 *  Bridge between [LoyaltyCard] and [WalletCardViewInfo]. Generates a Google Pay-esque card
 *  image for the drawable, and shows relevant info for the other values
 */
class WalletLoyaltyCardViewInfo(private val context: Context, private val loyaltyCard: LoyaltyCard, private val labelTypeface: Typeface, private val onClick: (LoyaltyCard) -> Boolean): WalletCardViewInfo {

    private companion object {
        private const val CARD_DRAWABLE_WIDTH = 745
        private const val CARD_DRAWABLE_HEIGHT = 468
        private const val CARD_LOGO_SIZE = 300
        private const val CARD_TEXT_SIZE = 123f
    }

    private val cachedCardDrawable = createCardDrawable(context)
    private val cachedCardIcon = getCardIcon(context)

    private val instructions = context.getString(R.string.wallet_loyalty_instructions)

    val id = loyaltyCard.valuableId

    override fun getCardId(): String {
        return "LOY" + loyaltyCard.valuableId
    }

    override fun getText(): CharSequence {
        return "${loyaltyCard.issuerName} $instructions"
    }

    override fun getCardDrawable(): Drawable {
        return cachedCardDrawable
    }

    override fun getContentDescription(): CharSequence {
        return loyaltyCard.redemptionInfo.contentDescription
    }

    override fun getIcon(): Drawable {
        return cachedCardIcon
    }

    override fun getPendingIntent(): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(String.format(GooglePayConstants.WALLET_DEEP_LINK_VALUABLE, loyaltyCard.valuableId))
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onClick(): Boolean {
        return onClick.invoke(loyaltyCard)
    }

    fun getLoyaltyCard() = loyaltyCard

    private fun createCardDrawable(context: Context): Drawable {
        val bitmap = Bitmap.createBitmap(CARD_DRAWABLE_WIDTH, CARD_DRAWABLE_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val logoBitmap = loyaltyCard.valuableImage?.let {
            Bitmap.createScaledBitmap(it, CARD_LOGO_SIZE, CARD_LOGO_SIZE, true).getRoundedBitmap()
        }
        //Background
        paint.isAntiAlias = true
        paint.color = loyaltyCard.backgroundColor
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, CARD_DRAWABLE_WIDTH.toFloat(), CARD_DRAWABLE_HEIGHT.toFloat(), paint)
        //Logo
        if(logoBitmap != null){
            canvas.drawBitmap(logoBitmap, (CARD_DRAWABLE_WIDTH / 2f) - (CARD_LOGO_SIZE / 2f), (CARD_DRAWABLE_HEIGHT / 2f) - (CARD_LOGO_SIZE / 2f), paint)
        }else{
            val foregroundColor = ContextCompat.getColor(context, R.color.quick_access_wallet_generic_foreground)
            paint.color = foregroundColor
            canvas.drawCircle((CARD_DRAWABLE_WIDTH / 2f), (CARD_DRAWABLE_HEIGHT / 2f), (CARD_LOGO_SIZE / 2f), paint)
            paint.textSize = CARD_TEXT_SIZE
            paint.typeface = labelTypeface
            paint.color = ContextCompat.getColor(context, R.color.quick_access_wallet_generic_text)
            canvas.drawCenteredText(Rect(0, 0, CARD_DRAWABLE_WIDTH, CARD_DRAWABLE_HEIGHT), paint, loyaltyCard.issuerName.substring(0, 1))
        }
        //Outline circle
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawCircle((CARD_DRAWABLE_WIDTH / 2f), (CARD_DRAWABLE_HEIGHT / 2f), (CARD_LOGO_SIZE / 2f), paint)
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun getCardIcon(context: Context): Drawable {
        val resource = when(loyaltyCard.redemptionInfo.type) {
            LoyaltyCard.RedemptionInfo.Type.QR_CODE, LoyaltyCard.RedemptionInfo.Type.AZTEC -> R.drawable.ic_card_type_qr
            else -> R.drawable.ic_card_type_barcode
        }
        return ContextCompat.getDrawable(context, resource)!!
    }

}
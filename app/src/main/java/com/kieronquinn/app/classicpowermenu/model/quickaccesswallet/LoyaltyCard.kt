package com.kieronquinn.app.classicpowermenu.model.quickaccesswallet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.zxing.BarcodeFormat
import com.kieronquinn.app.classicpowermenu.R
import com.google.type.Color as ProtoColor
import com.kieronquinn.app.classicpowermenu.model.protobuf.loyaltycard.LoyaltyCardProtos
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.LoyaltyCard.RedemptionInfo.Type.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoyaltyCard(
    val valuableId: String,
    val valuableImage: Bitmap? = null,
    val id: String,
    val title: String,
    val issuerName: String,
    @ColorInt val backgroundColor: Int,
    val redemptionInfo: RedemptionInfo
): Parcelable {

    @Parcelize
    data class RedemptionInfo(
        val identifier: String,
        private val _type: Int,
        val encodedValue: String,
        val displayText: String,
        val contentDescription: String,
        val type: Type? = values().find { it.protoOrdinal == _type }
    ): Parcelable {

        override fun toString(): String {
            return "RedemptionInfo identifier=$identifier type=$type encodedValue=$encodedValue displayText=$displayText contentDescription=$contentDescription"
        }

        enum class Type(internal val protoOrdinal: Int) {
            BARCODE_TYPE_UNSPECIFIED(0),
            AZTEC(2),
            CODE_39(3),
            CODE_128(5),
            CODABAR(6),
            DATA_MATRIX(7),
            EAN_8(8),
            EAN_13(9),
            ITF_14(10),
            PDF_417(11),
            QR_CODE(14),
            UPC_A(15),
            UPC_E(16),
            TEXT_ONLY(19),
            UNRECOGNIZED(-1)
        }

    }

    override fun toString(): String {
        return "LoyaltyCard valuableId=$valuableId image=$valuableImage id=$id title=$title issuerName=$issuerName backgroundColor=#${Integer.toHexString(backgroundColor)} redemptionInfo=$redemptionInfo"
    }

    fun isCodeSquare(): Boolean {
        return redemptionInfo.type.run {
            this == QR_CODE || this == AZTEC
        }
    }

    fun isBackgroundDark(): Boolean {
        return ColorUtils.calculateLuminance(backgroundColor) < 0.5
    }

    fun getTypeAsBarcodeFormat(): BarcodeFormat? {
        return when(redemptionInfo.type){
            BARCODE_TYPE_UNSPECIFIED -> null
            AZTEC -> BarcodeFormat.AZTEC
            CODE_39 -> BarcodeFormat.CODE_39
            CODABAR -> BarcodeFormat.CODABAR
            DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
            CODE_128 -> BarcodeFormat.CODE_128
            EAN_8 -> BarcodeFormat.EAN_8
            EAN_13 -> BarcodeFormat.EAN_13
            ITF_14 -> BarcodeFormat.ITF
            PDF_417 -> BarcodeFormat.PDF_417
            QR_CODE -> BarcodeFormat.QR_CODE
            UPC_A -> BarcodeFormat.UPC_A
            UPC_E -> BarcodeFormat.UPC_E
            TEXT_ONLY -> null
            UNRECOGNIZED -> null
            null -> null
        }
    }

}

fun LoyaltyCardProtos.LoyaltyCard_.extract(valuableImage: Bitmap?, context: Context): LoyaltyCard {
    val valuableId = this.id
    val id = this.issuerInfo.id
    val title = this.issuerInfo.title
    val issuerName = this.issuerInfo.issuerName
    val backgroundColor = this.issuerInfo?.backgroundColor?.toColor(Color.TRANSPARENT) ?: this.issuerInfo?.mainImageInfo?.color?.toColor(Color.TRANSPARENT)
        ?: ContextCompat.getColor(context, R.color.quick_access_wallet_generic_background)
    val redemptionInfo = this.redemptionInfo.extract()
    return LoyaltyCard(valuableId, valuableImage, id, title, issuerName, backgroundColor, redemptionInfo)
}

fun LoyaltyCardProtos.LoyaltyCard_RedemptionInfo.extract(): LoyaltyCard.RedemptionInfo {
    val identifier = this.identifier
    val type = this.barcode.type
    val encodedValue = this.barcode.encodedValue
    val displayText = this.barcode.displayText
    val label = this.barcode.label
    return LoyaltyCard.RedemptionInfo(identifier, type, encodedValue, displayText, label)
}

private fun ProtoColor.toColor(ignoreColor: Int? = null): Int? {
    return Color.valueOf(red, green, blue, alpha.value).toArgb().let {
        if(it == ignoreColor) null
        else it
    }
}

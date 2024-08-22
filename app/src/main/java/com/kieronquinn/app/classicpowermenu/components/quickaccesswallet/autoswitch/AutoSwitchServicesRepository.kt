package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.android.systemui.plugin.globalactions.wallet.WalletCardViewInfo
import com.kieronquinn.app.classicpowermenu.IClassicPowerMenu
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.GooglePayConstants
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.model.protobuf.loyaltycard.LoyaltyCardProtos
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.LoyaltyCard
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.WalletLoyaltyCardViewInfo
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.extract
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface LoyaltyCardsRepository {

    suspend fun getLoyaltyCards(onCardClicked: (LoyaltyCard) -> Boolean, ignoreSetting: Boolean = false): List<WalletCardViewInfo>?

}

class LoyaltyCardsRepositoryImpl(private val service: CPMServiceContainer, private val context: Context, private val settings: Settings): LoyaltyCardsRepository {

    private val isGooglePayInstalled by lazy {
        GooglePayConstants.isGooglePayInstalled(context.packageManager)
    }

    private val googleSansMedium by lazy {
        ResourcesCompat.getFont(context, R.font.google_sans_text_medium)!!
    }

    override suspend fun getLoyaltyCards(onCardClicked: (LoyaltyCard) -> Boolean, ignoreSetting: Boolean): List<WalletCardViewInfo>? {
        if((!settings.quickAccessWalletShowLoyaltyCards && !ignoreSetting) || !isGooglePayInstalled) return null
        return service.runWithService { service ->
            withContext(Dispatchers.IO) {
                context.withTemporaryFile {
                    service.googlePayDatabaseForLoyalty?.loadRemoteFile(it) ?: return@withTemporaryFile null
                    val database = SQLiteDatabase.openOrCreateDatabase(it, null)
                    val valuableImageFileNames = getValuableImageList(database)
                    getValuables(database, valuableImageFileNames, service)
                }
            }
        }?.map { WalletLoyaltyCardViewInfo(context, it, googleSansMedium, onCardClicked) }
    }

    private fun getValuableImageList(database: SQLiteDatabase): HashMap<String, String> {
        val valuableImageFileNames = HashMap<String, String>()
        val valuableImagesCursor = database.rawQuery("select valuable_id,file_name from valuable_images", null, null)
        valuableImagesCursor.moveToFirst()
        if(valuableImagesCursor.isAfterLast) return valuableImageFileNames
        do {
            if(valuableImagesCursor.isAfterLast) break
            valuableImageFileNames[valuableImagesCursor.getString(0)] = valuableImagesCursor.getString(1)
        }while (valuableImagesCursor.moveToNext())
        valuableImagesCursor.close()
        return valuableImageFileNames
    }

    private fun getValuables(database: SQLiteDatabase, valuableImageFileNames: HashMap<String, String>, service: IClassicPowerMenu): ArrayList<LoyaltyCard> {
        val loyaltyCards = ArrayList<LoyaltyCard>()
        //Vertical ID 1 = LOYALTY_CARD (See CommonProto.ValuableType in Google Pay)
        val cursor = database.rawQuery("select valuable_id,proto from valuables where vertical_id='1'", null, null)
        cursor.moveToFirst()
        if(cursor.isAfterLast) return loyaltyCards
        do {
            if(cursor.isAfterLast) break
            val valuableId = cursor.getString(0)
            val valuableIcon = valuableImageFileNames[valuableId]?.let { valuableImageFileName ->
                val bitmap = context.withTemporaryFile {
                    service.getGooglePayLoyaltyImageForId(valuableImageFileName)?.loadRemoteFile(it) ?: return@withTemporaryFile null
                    BitmapFactory.decodeFile(it.absolutePath)
                }
                bitmap
            }
            val blob = cursor.getBlob(1)
            val loyaltyCard = LoyaltyCardProtos.LoyaltyCard.parseFrom(blob)
            loyaltyCards.add(loyaltyCard.extract(valuableIcon, context))
        } while (cursor.moveToNext())
        cursor.close()
        return loyaltyCards
    }

    private fun ParcelFileDescriptor.loadRemoteFile(into: File){
        ParcelFileDescriptor.AutoCloseInputStream(this).buffered().use { stream ->
            stream.copyTo(into.outputStream())
        }
    }

    private fun <T> Context.withTemporaryFile(block: (File) -> T): T {
        val file = File(cacheDir, UUID.randomUUID().toString())
        val result = block.invoke(file)
        file.delete()
        return result
    }

}
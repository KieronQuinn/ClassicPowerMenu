package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.android.systemui.plugin.globalactions.wallet.WalletCardViewInfo
import com.google.internal.tapandpay.v1.valuables.SyncValuablesRequestProto.SyncValuablesRequest
import com.google.internal.tapandpay.v1.valuables.SyncValuablesRequestProto.SyncValuablesRequest.SyncValuablesRequestInner.Request
import com.google.internal.tapandpay.v1.valuables.SyncValuablesResponseProto
import com.google.internal.tapandpay.v1.valuables.SyncValuablesResponseProto.SyncValuablesResponse.Inner.Valuables.Valuable
import com.google.internal.tapandpay.v1.valuables.ValuableWrapperProto
import com.google.internal.tapandpay.v1.valuables.ValuableWrapperProto.ValuableWrapper
import com.google.protobuf.ByteString
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.GooglePayConstants
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.GoogleWalletRepository.SyncValuablesResult
import com.kieronquinn.app.classicpowermenu.components.settings.EncryptedSettings
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.LoyaltyCard
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.WalletLoyaltyCardViewInfo
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.database.WalletValuable
import com.kieronquinn.app.classicpowermenu.utils.extensions.CONTENT_TYPE_PROTOBUF
import com.kieronquinn.app.classicpowermenu.utils.extensions.compress
import com.kieronquinn.app.classicpowermenu.utils.extensions.toColor
import com.kieronquinn.app.classicpowermenu.utils.extensions.toRequestBody
import com.kieronquinn.app.classicpowermenu.utils.room.EncryptedValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.net.URL
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale


interface GoogleWalletRepository {

    suspend fun syncValuables(): SyncValuablesResult

    suspend fun getLoyaltyCards(onCardClicked: (LoyaltyCard) -> Boolean, ignoreSetting: Boolean = false): List<WalletCardViewInfo>?

    /**
     *  Current valuables from the database. Call [syncValuables] to trigger a sync.
     */
    fun getValuables(): Flow<List<LoyaltyCard>>


    enum class SyncValuablesResult {
        /**
         *  Sync was successful
         */
        SUCCESS,

        /**
         *  A non-fatal error, for example a network connection issue, occurred
         */
        ERROR,

        /**
         *  The request returned a bad auth response, and then token refresh also failed. Likely
         *  will require a re-log.
         */
        FATAL_ERROR
    }

}

class GoogleWalletRepositoryImpl(
    private val context: Context,
    private val settings: Settings,
    private val encryptedSettings: EncryptedSettings,
    private val googleApiRepository: GoogleApiRepository,
    private val valuablesDatabaseRepository: ValuablesDatabaseRepository
): GoogleWalletRepository {

    companion object {
        private const val HEADER_REQUEST = "ExoBBA8FCxQMCAcJAwYOCg0QEhYYHA=="
    }

    private val isGooglePayInstalled by lazy {
        GooglePayConstants.isGooglePayInstalled(context.packageManager)
    }

    private val googleSansMedium by lazy {
        ResourcesCompat.getFont(context, R.font.google_sans_text_medium)!!
    }

    init {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    override suspend fun syncValuables() = withContext(Dispatchers.IO) {
        syncValuablesInternal()
    }

    override suspend fun getLoyaltyCards(onCardClicked: (LoyaltyCard) -> Boolean, ignoreSetting: Boolean): List<WalletCardViewInfo>? {
        if((!settings.quickAccessWalletShowLoyaltyCards && !ignoreSetting) || !isGooglePayInstalled) return null
        return getValuables().first().map { WalletLoyaltyCardViewInfo(context, it, googleSansMedium, onCardClicked) }
    }

    override fun getValuables(): Flow<List<LoyaltyCard>> {
        return valuablesDatabaseRepository.getWalletValuables().map { it.map { it.toValuable() } }
    }

    private fun WalletValuable.toValuable(): LoyaltyCard {
        val valuable = ValuableWrapper.parseFrom(valuable.bytes)

        val imageBytes = image?.bytes
        val bitmap = if (imageBytes != null) BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) else null
        val loyaltyCard = LoyaltyCard(
            id,
            bitmap,
            valuable.loyaltyCard.issuerInfo.id,
            valuable.loyaltyCard.issuerInfo.title,
            valuable.loyaltyCard.issuerInfo.issuerName,
            valuable.loyaltyCard.groupingInfo.backgroundColor.toColor(),
            LoyaltyCard.RedemptionInfo(
                valuable.loyaltyCard.redemptionInfo.identifier,
                valuable.loyaltyCard.redemptionInfo.barcode.typeValue,
                valuable.loyaltyCard.redemptionInfo.barcode.encodedValue,
                valuable.loyaltyCard.redemptionInfo.barcode.displayText,
                valuable.loyaltyCard.redemptionInfo.barcode.displayText))
        return loyaltyCard
    }

    private val service = Retrofit.Builder()
        .baseUrl(GoogleWalletService.BASE_URL)
        .build()
        .create(GoogleWalletService::class.java)


    private fun createSyncValuablesRequest(currentValuables: List<Valuable>): SyncValuablesRequest {
        val request = SyncValuablesRequest.SyncValuablesRequestInner.newBuilder()
            .setRequest(buildRequest(currentValuables))
            .build()
        return SyncValuablesRequest.newBuilder()
            .setRequest(request)
            .build()
    }

    private fun buildRequest(currentValuables: List<Valuable>): Request {
        val cachedValuables = currentValuables.map {
            Request.CachedValuable.newBuilder()
                .setId(it.id)
                .setHash(it.hash)
                .build()
        }
        return Request.newBuilder()
            .setHeader(ByteString.copyFrom(Base64.decode(HEADER_REQUEST, Base64.DEFAULT)))
            .setTimezone(ZoneId.systemDefault().getDisplayName(
                TextStyle.FULL_STANDALONE, Locale.ENGLISH
            ))
            .addAllCachedValuable(cachedValuables)
            .build()
    }

    private fun GoogleWalletService.syncValuables(
        token: String,
        currentValuables: List<Valuable>
    ): SyncValuablesResponse {
        val requestBody = createSyncValuablesRequest(currentValuables).toByteArray()
            .toRequestBody(CONTENT_TYPE_PROTOBUF)
        return try {
            val response = syncValuables("Bearer $token", body = requestBody).execute()
            when(response.code()){
                200 -> {
                    response.body()?.let { body ->
                        SyncValuablesResponse.Success(body)
                    } ?: SyncValuablesResponse.GenericError
                }
                401 -> SyncValuablesResponse.BadAuthentication
                else -> SyncValuablesResponse.GenericError
            }
        }catch (e: Exception){
            SyncValuablesResponse.GenericError
        }
    }

    private suspend fun syncValuablesInternal(isRetry: Boolean = false): SyncValuablesResult {
        val token = getToken() ?: return SyncValuablesResult.FATAL_ERROR
        val result = loadValuables(token)
        return when {
            result == SyncValuablesResult.FATAL_ERROR && !isRetry -> {
                encryptedSettings.walletToken = ""
                syncValuablesInternal(true)
            }
            else -> result
        }
    }

    private suspend fun getToken(): String? {
        val token = encryptedSettings.walletToken
        if(token.isNotEmpty()) return token
        return googleApiRepository.getToken(GoogleApiRepository.Scope.WALLET)?.also {
            encryptedSettings.walletToken = it
        }
    }

    private suspend fun getCurrentValuables(): List<Valuable> {
        return valuablesDatabaseRepository.getWalletValuables().first().map {
            Valuable.newBuilder()
                .setId(it.id)
                .setHash(it.hash)
                .build()
        }
    }

    private suspend fun loadValuables(token: String): SyncValuablesResult {
        val valuables = service.run {
            syncValuables(token, getCurrentValuables())
        }
        return when(valuables) {
            is SyncValuablesResponse.Success -> {
                val bytes = valuables.body.bytes()
                val response = SyncValuablesResponseProto.SyncValuablesResponse.parseFrom(bytes)
                commitValuables(response)
                SyncValuablesResult.SUCCESS
            }
            is SyncValuablesResponse.GenericError -> SyncValuablesResult.ERROR
            is SyncValuablesResponse.BadAuthentication -> SyncValuablesResult.FATAL_ERROR
        }
    }

    private suspend fun commitValuables(response: SyncValuablesResponseProto.SyncValuablesResponse) {
        val valuableList = response.inner.valuables.valuableList
        valuableList.forEach {
            // For now we only care about loyalty cards
            if(it.hash != 0L && it.valuable.isValidValuable()) {

                var image: Bitmap? = null
                if (it.valuable.loyaltyCard.groupingInfo.groupingImage.uri.isNotEmpty()) {
                    val url = URL(it.valuable.loyaltyCard.groupingInfo.groupingImage.uri)
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                }

                val valuable = WalletValuable(
                    it.id,
                    it.hash,
                    EncryptedValue(it.valuable.toByteArray()),
                    if (image != null) image.compress()?.let { compressed -> EncryptedValue(compressed) } else null
                )
                valuablesDatabaseRepository.addWalletValuable(valuable)
            }else{
                valuablesDatabaseRepository.deleteWalletValuable(it.id)
            }
        }
    }

    private fun ValuableWrapper.isValidValuable(): Boolean {
        if(loyaltyCard == null) return false
        if(loyaltyCard.issuerInfo.issuerName.isNullOrBlank()) return false
        return true
    }

    sealed class SyncValuablesResponse {
        data class Success(val body: ResponseBody): SyncValuablesResponse()
        object BadAuthentication: SyncValuablesResponse()
        object GenericError: SyncValuablesResponse()
    }

}

interface GoogleWalletService {

    companion object {
        const val BASE_URL = "https://pay-users-pa.googleapis.com/"
    }

    @POST("g/valuables/syncvaluables")
    fun syncValuables(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody
    ): Call<ResponseBody>

}
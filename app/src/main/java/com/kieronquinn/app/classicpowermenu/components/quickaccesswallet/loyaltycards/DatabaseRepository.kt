package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards

import com.kieronquinn.app.classicpowermenu.components.settings.RoomEncryptedSettingsRepository
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.database.WalletDatabase
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.database.WalletValuable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface ValuablesDatabaseRepository {

    fun getWalletValuables(): Flow<List<WalletValuable>>
    fun getWalletValuableById(id: String): Flow<WalletValuable?>

    suspend fun addWalletValuable(walletValuable: WalletValuable)
    suspend fun deleteWalletValuable(id: String)

}

class ValuablesDatabaseRepositoryImpl(
    database: WalletDatabase
): ValuablesDatabaseRepository, RoomEncryptedSettingsRepository.RoomEncryptionFailedCallback {

    private val walletValuable = database.walletValuableDao()
    private val databaseLock = Mutex()

    override fun getWalletValuables() = walletValuable.getAll()

    override fun getWalletValuableById(id: String): Flow<WalletValuable?> {
        return walletValuable.getValuableById(id)
    }

    override suspend fun addWalletValuable(
        walletValuable: WalletValuable
    ) = withContext(Dispatchers.IO) {
        databaseLock.withLock {
            this@ValuablesDatabaseRepositoryImpl.walletValuable.insert(walletValuable)
        }
    }

    override suspend fun deleteWalletValuable(id: String) = withContext(Dispatchers.IO) {
        databaseLock.withLock {
            this@ValuablesDatabaseRepositoryImpl.walletValuable.delete(id)
        }
    }

    override fun onEncryptionFailed() {
        walletValuable.clear()
    }

}
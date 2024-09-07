package com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kieronquinn.app.classicpowermenu.utils.room.EncryptedValueConverter
import com.kieronquinn.app.classicpowermenu.utils.room.GsonSetConverter

@Database(entities = [
    //TargetData::class,
    WalletValuable::class
], version = 1, exportSchema = false)
@TypeConverters(EncryptedValueConverter::class, GsonSetConverter::class)
abstract class WalletDatabase: RoomDatabase() {

    companion object {
        fun getDatabase(context: Context): WalletDatabase {
            return Room.databaseBuilder(
                context,
                WalletDatabase::class.java,
                "wallet"
            ).build()
        }
    }

    abstract fun walletValuableDao(): WalletValuableDao
}
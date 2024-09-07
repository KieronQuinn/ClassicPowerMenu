package com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletValuableDao {

    @Query("select * from `WalletValuable`")
    fun getAll(): Flow<List<WalletValuable>>

    @Query("select * from `WalletValuable` where id=:id")
    fun getValuableById(id: String): Flow<WalletValuable?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(valuable: WalletValuable)

    @Query("delete from `WalletValuable` where id=:id")
    fun delete(id: String)

    @Query("delete from `WalletValuable`")
    fun clear()

}
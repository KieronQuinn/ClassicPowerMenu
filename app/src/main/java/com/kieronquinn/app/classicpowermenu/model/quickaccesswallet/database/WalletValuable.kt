package com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kieronquinn.app.classicpowermenu.utils.room.EncryptedValue

@Entity
data class WalletValuable(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo("hash")
    val hash: Long,
    @ColumnInfo("proto")
    val valuable: EncryptedValue,
    @ColumnInfo("image")
    val image: EncryptedValue?
)
package com.kieronquinn.app.classicpowermenu.utils.extensions

import com.android.systemui.plugin.globalactions.wallet.WalletCardViewInfo
import com.kieronquinn.app.classicpowermenu.model.quickaccesswallet.WalletLoyaltyCardViewInfo

fun WalletCardViewInfo.getLoyaltyIdOrNull(): String? {
    return if(this is WalletLoyaltyCardViewInfo) this.id
    else null
}
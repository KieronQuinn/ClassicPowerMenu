package com.android.systemui.plugins.loyaltycards

import com.android.systemui.plugin.globalactions.wallet.WalletCardViewInfo

data class WalletLoyaltyCardCallback(val method: (list: ArrayList<WalletCardViewInfo>, callback: Runnable) -> Unit)
package com.kieronquinn.app.classicpowermenu.utils.extensions

import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs

val AppBarLayout.scrollPercentage
    get() = callbackFlow {
        val changeListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            trySend(abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange)
        }
        addOnOffsetChangedListener(changeListener)
        awaitClose {
            removeOnOffsetChangedListener(changeListener)
        }
    }
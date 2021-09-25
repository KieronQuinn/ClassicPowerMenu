package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.hypot

private val RecyclerView.isVerticallyScrolled
    get() = computeVerticalScrollOffset() > 0

val RecyclerView.isScrolled
    get() = callbackFlow {
        var currentValue: Boolean? = null
        val listener = object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isScrolled = isVerticallyScrolled
                if(currentValue != isScrolled){
                    currentValue = isScrolled
                    trySend(isScrolled)
                }
            }
        }
        addOnScrollListener(listener)
        //Send start state
        trySend(isVerticallyScrolled)
        awaitClose {
            removeOnScrollListener(listener)
        }
    }

val RecyclerView.Adapter<*>.changed
    get() = callbackFlow {
        val observer = object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                trySend(Unit)
            }
        }
        registerAdapterDataObserver(observer)
        //Prevent blocking
        trySend(Unit)
        awaitClose {
            unregisterAdapterDataObserver(observer)
        }
    }

fun RecyclerView.findChildViewUnderRelative(x: Float, y: Float): View? {
    val position = Rect().apply {
        getGlobalVisibleRect(this)
    }
    if(!position.contains(x.toInt(), y.toInt())) return null
    val relativeX = x - position.left
    val relativeY = y - position.top
    return findChildViewUnder(relativeX, relativeY)
}

fun RecyclerView.LayoutManager.getClosestItemToPosition(x: Float, y: Float): View? {
    if(itemCount == 0) return null
    val distances = ArrayList<Pair<Int, View>>()
    for(i in 0 until itemCount){
        val view = getChildAt(i) ?: continue
        val center = view.getCenter()
        distances.add(Pair(hypot((center.x - x).toDouble(), (center.y - y).toDouble()).toInt(), view))
    }
    return distances.minByOrNull { it.first }?.second
}
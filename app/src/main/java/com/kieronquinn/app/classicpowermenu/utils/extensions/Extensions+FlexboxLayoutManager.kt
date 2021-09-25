package com.kieronquinn.app.classicpowermenu.utils.extensions

import com.google.android.flexbox.FlexboxLayoutManager

/**
 *  Sets the alpha of all visible items that are not on the first row
 */
fun FlexboxLayoutManager.setSecondaryAlpha(alpha: Float){
    with(flexLines){
        if(isEmpty()) return
        val firstLineLength = get(0).itemCount
        //Check if we've only got one row
        if(itemCount == firstLineLength) return
        //Set the alpha of all items that are not on the first row
        for(i in firstLineLength until itemCount){
            getChildAt(i)?.alpha = alpha
        }
    }
}
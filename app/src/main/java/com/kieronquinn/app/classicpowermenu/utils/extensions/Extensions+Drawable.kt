package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable

fun Drawable.animateToVisible(duration: Long = 250L): ValueAnimator {
    return ValueAnimator.ofInt(alpha, 255).apply {
        this.duration = duration
        addUpdateListener {
            alpha = it.animatedValue as Int
        }
        start()
    }
}

fun Drawable.animateToInvisible(duration: Long = 250L): ValueAnimator {
    return ValueAnimator.ofInt(alpha, 0).apply {
        this.duration = duration
        addUpdateListener {
            alpha = it.animatedValue as Int
        }
        start()
    }
}
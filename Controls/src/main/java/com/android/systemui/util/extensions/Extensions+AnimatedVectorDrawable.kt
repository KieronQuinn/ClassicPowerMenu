package com.android.systemui.util.extensions

import android.graphics.drawable.AnimatedVectorDrawable

fun AnimatedVectorDrawable.forceAnimationOnUI(){
    AnimatedVectorDrawable::class.java.getMethod("forceAnimationOnUI").invoke(this)
}
package com.kieronquinn.app.classicpowermenu.components.blur

import android.content.res.Resources
import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi
import com.kieronquinn.app.classicpowermenu.R

@RequiresApi(Build.VERSION_CODES.S)
class BlurProvider31(resources: Resources): BlurProvider() {

    override val minBlurRadius by lazy {
        resources.getDimensionPixelSize(R.dimen.min_window_blur_radius).toFloat()
    }

    override val maxBlurRadius by lazy {
        resources.getDimensionPixelSize(R.dimen.max_window_blur_radius).toFloat()
    }

    override fun applyDialogBlur(dialogWindow: Window, appWindow: Window, ratio: Float) {
        applyBlurToWindow(dialogWindow, ratio)
    }

    override fun applyBlurToWindow(window: Window, ratio: Float) {
        val radius = blurRadiusOfRatio(ratio)
        window.attributes.blurBehindRadius = radius
    }

}
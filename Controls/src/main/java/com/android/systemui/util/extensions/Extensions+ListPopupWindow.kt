package com.android.systemui.util.extensions

import android.widget.ListPopupWindow

fun ListPopupWindow.dismissImmediate(){
    ListPopupWindow::class.java.getMethod("dismissImmediate").invoke(this)
}
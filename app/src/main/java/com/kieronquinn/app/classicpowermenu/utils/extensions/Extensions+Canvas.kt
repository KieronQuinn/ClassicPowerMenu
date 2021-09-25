package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

fun Canvas.drawCenteredText(rect: Rect, paint: Paint, text: String) {
    getClipBounds(rect)
    val cHeight: Int = rect.height()
    val cWidth: Int = rect.width()
    paint.textAlign = Paint.Align.LEFT
    paint.getTextBounds(text, 0, text.length, rect)
    val x: Float = cWidth / 2f - rect.width() / 2f - rect.left
    val y: Float = cHeight / 2f + rect.height() / 2f - rect.bottom
    drawText(text, x, y, paint)
}
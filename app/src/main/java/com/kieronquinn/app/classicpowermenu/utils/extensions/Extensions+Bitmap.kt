package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.graphics.*
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Compresses the bitmap to a byte array for serialization.
 */
fun Bitmap.compress(recycleBitmap: Boolean = false): ByteArray? {
    val out = ByteArrayOutputStream(getExpectedBitmapSize())
    return try {
        compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        out.toByteArray().also {
            if(recycleBitmap) recycle()
        }
    } catch (e: IOException) {
        null
    }
}

fun ByteArray.toBitmap(): Bitmap? {
    return BitmapFactory.decodeByteArray(this, 0, size)
}

/**
 * Try go guesstimate how much space the icon will take when serialized to avoid unnecessary
 * allocations/copies during the write (4 bytes per pixel).
 */
private fun Bitmap.getExpectedBitmapSize(): Int {
    return width * height * 4
}

/**
 * Convert square bitmap to circle
 * @param Bitmap - square bitmap
 * @return circle bitmap
 */
fun Bitmap.getRoundedBitmap(): Bitmap {
    val output = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint()
    val rect = Rect(0, 0, this.width, this.height)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    canvas.drawCircle(this.width / 2f, this.height / 2f, this.width / 2f, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    return output
}
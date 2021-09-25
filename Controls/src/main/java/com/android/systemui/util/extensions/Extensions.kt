package com.android.systemui.util.extensions

import com.android.internal.util.IndentingPrintWriter
import java.io.PrintWriter

/**
 * If `this` is an [IndentingPrintWriter], it will process block inside an indentation level.
 *
 * If not, this will just process block.
 */
inline fun PrintWriter.indentIfPossible(block: PrintWriter.() -> Unit) {
    if (this is IndentingPrintWriter) increaseIndent()
    block()
    if (this is IndentingPrintWriter) decreaseIndent()
}
package com.android.systemui.util.extensions

import android.os.Environment
import java.io.File

fun Environment_buildPath(file: File, vararg segments: String): File {
    return Environment::class.java.getMethod("buildPath", File::class.java, Array<String>::class.java).invoke(null, file, segments) as File
}
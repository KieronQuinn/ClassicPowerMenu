package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.content.Intent

private const val KEY_FROM_SELF = "from_self"

const val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

fun Intent.isFromSelf(): Boolean {
    return getBooleanExtra(KEY_FROM_SELF, false)
}

fun Intent.addFromSelf() = apply {
    return putExtra(KEY_FROM_SELF, false)
}
package com.android.systemui

import android.content.Context

class Prefs {

    companion object {
        fun getInt(context: Context, key: String, default: Int): Int {
            //TODO
            return default
        }

        fun putInt(context: Context, key: String, value: Int) {
            //TODO
        }

        const val CONTROLS_STRUCTURE_SWIPE_TOOLTIP_COUNT = "ControlsStructureSwipeTooltipCount"
    }
}
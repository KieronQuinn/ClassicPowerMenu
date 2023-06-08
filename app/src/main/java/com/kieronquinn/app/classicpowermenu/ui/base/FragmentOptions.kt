package com.kieronquinn.app.classicpowermenu.ui.base

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

interface BackAvailable
interface Root
interface AutoExpandOnRotate

interface StandaloneFragment {
    val onBackPressed: () -> Unit
}

interface ProvidesOverflow {
    fun inflateMenu(menuInflater: MenuInflater, menu: Menu)
    fun onMenuItemSelected(menuItem: MenuItem): Boolean
}
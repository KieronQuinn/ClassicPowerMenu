package com.kieronquinn.app.classicpowermenu.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openLink(url: String){
    try {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    }catch (e: ActivityNotFoundException){
        //No browser
    }
}

object Links {
    const val LINK_XDA = "https://kieronquinn.co.uk/redirect/ClassicPowerMenu/xda"
    const val LINK_TWITTER = "https://kieronquinn.co.uk/redirect/ClassicPowerMenu/twitter"
    const val LINK_DONATE = "https://kieronquinn.co.uk/redirect/ClassicPowerMenu/donate"
    const val LINK_GITHUB = "https://kieronquinn.co.uk/redirect/ClassicPowerMenu/github"
}
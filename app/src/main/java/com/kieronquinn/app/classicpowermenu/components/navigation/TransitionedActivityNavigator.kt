package com.kieronquinn.app.classicpowermenu.components.navigation

import android.app.Activity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

@Navigator.Name("sceneTransitionActivity")
class TransitionedActivityNavigator(private val activity: Activity): ActivityNavigator(activity) {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        return super.navigate(destination, args, navOptions, Extras.Builder().apply {
            setActivityOptions(ActivityOptionsCompat.makeSceneTransitionAnimation(activity))
        }.build())
    }

}
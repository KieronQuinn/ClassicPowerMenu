package com.kieronquinn.app.classicpowermenu.ui.screens.settings.update

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieronquinn.app.classicpowermenu.components.github.UpdateChecker
import com.kieronquinn.app.classicpowermenu.components.navigation.ContainerNavigation
import kotlinx.coroutines.launch

abstract class UpdateAvailableBottomSheetViewModel: ViewModel() {

    abstract fun onDismissClicked()
    abstract fun onDownloadClicked()
    abstract fun onOpenInGitHubClicked(update: UpdateChecker.Update)

}

class UpdateAvailableBottomSheetViewModelImpl(private val navigation: ContainerNavigation): UpdateAvailableBottomSheetViewModel() {

    override fun onDownloadClicked() {
        viewModelScope.launch {
            navigation.navigate(UpdateAvailableBottomSheetFragmentDirections.actionUpdateAvailableBottomSheetFragmentToUpdateDownloadBottomSheetFragment())
        }
    }

    override fun onDismissClicked() {
        viewModelScope.launch {
            navigation.navigateBack()
        }
    }

    override fun onOpenInGitHubClicked(update: UpdateChecker.Update) {
        viewModelScope.launch {
            navigation.navigate(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(update.releaseUrl)
            })
        }
    }

}
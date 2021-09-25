package com.kieronquinn.app.classicpowermenu.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.classicpowermenu.R
import com.kieronquinn.app.classicpowermenu.components.blur.BlurProvider
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import org.koin.android.ext.android.inject

/**
 *  A fake "Top Sheet", behaves like a dialog, but is stuck to the top of the screen and has a slide
 *  open/close animation, like a reverse bottom sheet. No drag.
 */
abstract class BaseTopSheetFragment<T: ViewBinding>(inflate: (LayoutInflater, ViewGroup?, Boolean) -> T): BaseDialogFragment<T>(inflate) {

    private val blurProvider by inject<BlurProvider>()
    internal val settings by inject<Settings>()

    private var isBlurShowing = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.TopSheet_Dialog)
        dialog.window?.let {
            it.setGravity(Gravity.TOP)
            it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.top_sheet_rounded_bg)?.apply {
                if(settings.useMonet) {
                    setTint(monet.getBackgroundColor(requireContext()))
                }
            })
            it.attributes.windowAnimations = R.style.TopSheetDialogAnimation
            ViewCompat.setOnApplyWindowInsetsListener(it.decorView) { view, insets ->
                val navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                view.updatePadding(left = navigationInsets.left, right = navigationInsets.right)
                insets
            }
        }
        return dialog
    }

}
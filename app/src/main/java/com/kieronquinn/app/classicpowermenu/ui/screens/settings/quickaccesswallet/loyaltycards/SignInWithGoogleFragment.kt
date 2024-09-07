package com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.loyaltycards

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.kieronquinn.app.classicpowermenu.databinding.FragmentSignInWithGoogleBinding
import com.kieronquinn.app.classicpowermenu.ui.base.BackAvailable
import com.kieronquinn.app.classicpowermenu.ui.base.BoundFragment
import com.kieronquinn.app.classicpowermenu.utils.extensions.whenResumed
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.loyaltycards.SignInWithGoogleViewModel.State
import com.kieronquinn.app.classicpowermenu.utils.extensions.getCookies
import com.kieronquinn.app.classicpowermenu.utils.extensions.onApplyInsets

class SignInWithGoogleFragment : BoundFragment<FragmentSignInWithGoogleBinding>(FragmentSignInWithGoogleBinding::inflate), BackAvailable{

    companion object {
        private const val URL_EMBEDDED_SIGN_IN = "https://accounts.google.com/EmbeddedSetup"
    }

    private val viewModel by viewModel<SignInWithGoogleViewModel>()
    private val cookieManager = CookieManager.getInstance()

    private val webViewClient = object: WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            checkForCookie(url)
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            checkForCookie(url)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        setupState()
        setupInsets()
        if(savedInstanceState == null){
            cookieManager.removeAllCookies(null)
            cookieManager.removeSessionCookies(null)
            binding.signInWithGoogleWebview.loadUrl(URL_EMBEDDED_SIGN_IN)
        }else{
            binding.signInWithGoogleWebview.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.signInWithGoogleWebview.saveState(outState)
    }

    private fun setupWebView() = with(binding.signInWithGoogleWebview) {
        cookieManager.acceptThirdPartyCookies(this)
        settings.run {
            javaScriptEnabled = true
        }
        webViewClient = this@SignInWithGoogleFragment.webViewClient
    }

    fun onBackPressed(): Boolean {
        return if(binding.signInWithGoogleWebview.canGoBack()){
            binding.signInWithGoogleWebview.goBack()
            true
        }else false
    }

    private fun checkForCookie(url: String) {
        cookieManager.getCookies(url)["oauth_token"]?.let {
            viewModel.setCookie(it)
        }
    }

    private fun setupState() {
        handleState(viewModel.state.value)
        whenResumed {
            viewModel.state.collect {
                handleState(it)
            }
        }
    }

    private fun handleState(state: State) {
        when(state){
            is State.SignIn -> {
                //No-op, don't break WebView state
            }
            is State.Success -> {
                viewModel.close()
            }
            is State.Error -> {
                Toast.makeText(
                    requireContext(), "R.string.sign_in_with_google_error", Toast.LENGTH_LONG
                ).show()
                viewModel.close()
            }
        }
    }

    private fun setupInsets() = with(binding.signInWithGoogleWebview) {
        onApplyInsets { view, insets ->
            view.updateLayoutParams<FrameLayout.LayoutParams> {
                updateMargins(bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom)
            }
        }
    }

}
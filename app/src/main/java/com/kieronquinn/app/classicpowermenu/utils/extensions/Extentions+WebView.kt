package com.kieronquinn.app.classicpowermenu.utils.extensions


import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private object WebViewClient: WebViewClient()

fun CookieManager.getCookies(url: String): Map<String, String> {
    val cookieString = getCookie(url)
    return when {
        cookieString.isNullOrBlank() -> emptyList()
        !cookieString.contains(";") -> listOf(cookieString.trim())
        else -> {
            cookieString.split(";")
        }
    }.associate {
        it.parseCookie()
    }
}

fun String.parseCookie(): Pair<String, String> {
    val split = indexOf("=")
    val name = substring(0, split).trim()
    val value = substring(split + 1, length)
    return Pair(name, value)
}

suspend fun WebView.getHtml(): String = suspendCoroutine {
    evaluateJavascript("(function(){return window.document.body.outerHTML})();") { html ->
        it.resume(html)
    }
}

suspend fun WebView.load(
    url: String,
    timeout: Long = 10_000L
): Boolean = suspendCancellableCoroutineWithTimeout(timeout) {
    webViewClient = object: WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            webViewClient = WebViewClient
            it.resume(true)
        }
    }
    loadUrl(url)
    it.invokeOnCancellation {
        stopLoading()
    }
} ?: false
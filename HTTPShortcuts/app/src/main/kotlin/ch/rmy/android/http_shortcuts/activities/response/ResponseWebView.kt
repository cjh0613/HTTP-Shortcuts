package ch.rmy.android.http_shortcuts.activities.response

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import ch.rmy.android.http_shortcuts.extensions.consume
import ch.rmy.android.http_shortcuts.extensions.mapIf
import ch.rmy.android.http_shortcuts.extensions.openURL
import ch.rmy.android.http_shortcuts.utils.UserAgentUtil

class ResponseWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : WebView(context, attrs) {

    init {
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String) = consume {
                context.openURL(url)
            }
        }

        with(settings) {
            setAppCacheEnabled(false)
            cacheMode = WebSettings.LOAD_NO_CACHE
            javaScriptEnabled = false
            saveFormData = false
            allowContentAccess = false
            allowFileAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
            userAgentString = UserAgentUtil.userAgent
        }
    }

    fun loadFromString(data: String, baseUrl: String?) {
        val url = baseUrl
            ?.mapIf(!baseUrl.endsWith("/")) {
                plus("/")
            }
        loadDataWithBaseURL(url, data, "text/html", "UTF-8", null)
    }

}
package com.hanto.hook.ui.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.hanto.hook.BaseActivity
import com.hanto.hook.databinding.ActivityWebviewBinding

class WebViewActivity : BaseActivity() {

    companion object {
        const val TAG = "WebViewActivity"
        const val EXTRA_URL = "HOOK_URL"
    }

    private lateinit var binding: ActivityWebviewBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.mainWebView

        binding.ivWebViewBackButton.setOnClickListener {
            onBackPressed()
        }

        val url = intent.getStringExtra(EXTRA_URL)
        if (url.isNullOrBlank()) {
            Toast.makeText(this, "URL을 전달하지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupWebView(url)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        webView.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        webView.destroy()
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        webView.apply {
            loadUrl(url)
            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest?,
                    error: WebResourceError
                ) {
                    Log.d(TAG, "Error loading web page: ${error.description}")
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val urlToString = request.url.toString()
                    Log.d(TAG, "Navigating to URL: $urlToString")

                    if (urlToString.startsWith("http") || urlToString.startsWith("https")) {
                        return false
                    }
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToString))
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Log.e(TAG, "지원되지 않는 URL 스킴: $urlToString", e)
                    }
                    return true
                }
            }

            settings.apply {
                javaScriptEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                domStorageEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }

            addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun someFunctionFromJavaScript(data: String) {
                    Log.d(TAG, "Received data from JavaScript: $data")
                }
            }, "Android")
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

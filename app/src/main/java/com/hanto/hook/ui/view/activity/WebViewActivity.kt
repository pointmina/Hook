package com.hanto.hook.ui.view.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.core.net.toUri
import com.hanto.hook.R
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
            Toast.makeText(this, getString(R.string.plz_input_url), Toast.LENGTH_SHORT).show()
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

                    if (urlToString.startsWith("http://") || urlToString.startsWith("https://")) {
                        return false
                    }

                    return handleCustomScheme(urlToString)
                }

                @Suppress("DEPRECATION")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    Log.d(TAG, "Navigating to URL (deprecated): $url")

                    // HTTP/HTTPS URL인 경우 웹뷰에서 로드
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        return false
                    }

                    // 커스텀 스킴인 경우 앱 실행 시도
                    return handleCustomScheme(url)
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

    private fun handleCustomScheme(url: String): Boolean {
        Log.d(TAG, "handleCustomScheme 호출됨: $url")

        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                startActivity(intent)
                Log.d(TAG, "앱 실행 성공: $url")
                return true
            } catch (e: ActivityNotFoundException) {
                Log.d(TAG, "앱이 설치되어 있지 않음: $url")
                return true
            }

        } catch (e: Exception) {
            Log.e(TAG, "커스텀 스킴 처리 중 오류: $url", e)
            return true
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
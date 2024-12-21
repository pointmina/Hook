package com.hanto.hook.ui.view

import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.hanto.hook.BaseActivity
import com.hanto.hook.databinding.ActivityWebviewBinding

class WebViewActivity : BaseActivity() {

    val TAG = "WebViewActivity"
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate")
        super.onCreate(savedInstanceState)

        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.mainWebView

        val url = intent.getStringExtra(EXTRA_URL) ?: run {
            Toast.makeText(this, "URL을 전달하지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // WebView 설정
        setupWebView(url)
    }

    override fun onPause() {
        Log.d(TAG,"onPause")
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        Log.d(TAG,"onResume")
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        super.onDestroy()
    }

    private fun setupWebView(url: String) {
        webView.apply {
            loadUrl(url)
            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    Toast.makeText(
                        applicationContext,
                        "웹 페이지 로딩 실패: $description",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            settings.apply {
                javaScriptEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
            }

            webView.addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun someFunctionFromJavaScript(data: String) {
                }
            }, "Android")
        }
    }

    companion object {
        const val EXTRA_URL = "HOOK_URL"
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

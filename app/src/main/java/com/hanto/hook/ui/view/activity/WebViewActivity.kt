package com.hanto.hook.ui.view.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
                // 쿠팡 등 일부 사이트는 WebView 표시(" wv")가 붙은 UA를 비정상
                // 클라이언트로 간주해 접근을 차단한다. 일반 브라우저 UA로 보이도록
                // 표시만 제거한다.
                userAgentString = userAgentString.replace("; wv", "")
            }
        }
    }

    private fun handleCustomScheme(url: String): Boolean {
        Log.d(TAG, "handleCustomScheme 호출됨: $url")

        try {
            // intent://...#Intent;scheme=...;package=...;end 형식은 일반 Uri.parse로
            // 해석할 수 없다(scheme=intent인 불투명 Uri가 되어 버림). Chrome/Play
            // 스토어가 발급하는 이 형식은 Intent.parseUri(URI_INTENT_SCHEME)로만
            // package/scheme 등의 필드가 올바르게 채워진다.
            val intent = if (url.startsWith("intent://")) {
                Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            } else {
                Intent(Intent.ACTION_VIEW, url.toUri())
            }

            // intent:// 페이로드는 component/package/selector 필드를 임의로 지정할 수 있어,
            // 신뢰할 수 없는 웹 콘텐츠가 우리 앱 자신의(비공개일 수도 있는) 컴포넌트를
            // 조작된 extra와 함께 실행시키는 데 악용될 수 있다. 자기 자신을 대상으로 하거나
            // selector로 다른 intent를 중첩 지정하는 경우는 차단한다.
            val targetsSelf = intent.`package` == packageName ||
                intent.component?.packageName == packageName ||
                intent.selector?.`package` == packageName ||
                intent.selector?.component?.packageName == packageName
            if (targetsSelf) {
                Log.w(TAG, "자체 앱을 대상으로 하는 intent 스킴 차단: $url")
                return true
            }
            intent.selector = null
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                startActivity(intent)
                Log.d(TAG, "앱 실행 성공: $url")
                return true
            } catch (e: ActivityNotFoundException) {
                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                if (fallbackUrl != null) {
                    Log.d(TAG, "앱이 설치되어 있지 않음, 브라우저 폴백으로 이동: $fallbackUrl")
                    webView.loadUrl(fallbackUrl)
                } else {
                    Log.d(TAG, "앱이 설치되어 있지 않음: $url")
                }
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
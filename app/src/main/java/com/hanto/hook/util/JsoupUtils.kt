package com.hanto.hook.util

import android.util.Log
import org.jsoup.Jsoup

object JsoupUtils {
    private const val TAG = "JsoupUtils"
    private const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
    private const val TIMEOUT_MILLIS = 5000 // 5초 타임아웃

    /**
     * URL에서 og:image 태그의 content를 추출합니다.
     * @param url 타겟 URL
     * @return 이미지 URL 문자열 (없거나 실패 시 null)
     */
    fun getOgImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null

        return try {
            val doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MILLIS)
                .get()

            // 1. og:image 태그 시도
            var ogImage = doc.select("meta[property=og:image]").attr("content")

            // 2. 실패 시 twitter:image 태그 시도 (보완책)
            if (ogImage.isNullOrBlank()) {
                ogImage = doc.select("meta[name=twitter:image]").attr("content")
            }

            // 3. 그래도 없으면 null 반환
            if (ogImage.isNullOrBlank()) null else ogImage

        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse OG tag from $url: ${e.message}")
            null
        }
    }
}
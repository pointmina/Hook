package com.hanto.hook.util

/**
 * URL 관련 유틸리티 클래스
 */
object UrlUtils {

    private const val URL_PATTERN = "\\bhttps?://\\S+"
    private const val HTTP_PREFIX = "http://"
    private const val HTTPS_PREFIX = "https://"

    /**
     * 텍스트에서 URL을 추출합니다.
     * @param text 검색할 텍스트
     * @return 발견된 첫 번째 URL, 없으면 null
     */
    fun extractUrlFromText(text: String): String? {
        val urlPattern = Regex(URL_PATTERN)
        return urlPattern.find(text)?.value
    }

    /**
     * 문자열이 유효한 URL인지 확인합니다.
     * @param url 확인할 URL 문자열
     * @return 유효한 URL이면 true
     */
    fun isValidUrl(url: String): Boolean {
        return url.isNotBlank() &&
                !url.contains(" ") &&
                (url.startsWith(HTTP_PREFIX) || url.startsWith(HTTPS_PREFIX))
    }

    /**
     * URL에 프로토콜이 없으면 https://를 추가합니다.
     * @param url 처리할 URL
     * @return 프로토콜이 포함된 URL
     */
    fun ensureProtocol(url: String): String {
        return when {
            url.startsWith(HTTP_PREFIX) || url.startsWith(HTTPS_PREFIX) -> url
            url.isNotBlank() -> "$HTTPS_PREFIX$url"
            else -> url
        }
    }
}
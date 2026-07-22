package com.hanto.hook.data.remote

import android.util.Log
import com.hanto.hook.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

/**
 * Jsoup을 사용해 링크의 OG 메타데이터를 조회한다.
 * 스크래핑 세부사항(User-Agent, 타임아웃, 파싱)을 이 클래스에 격리한다.
 */
class MetadataDataSource @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val TAG = "MetadataDataSource"
        private const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
        private const val TIMEOUT_MILLIS = 5000

        // og:image/twitter:image는 항상 <head>에 있으므로 페이지 전체를 받을
        // 필요가 없다. 큰 페이지(쇼핑몰, SNS 등)에서 본문까지 통째로 내려받는
        // 비용을 없애기 위해 응답 크기를 제한한다. 다만 인스타그램/쓰레드류 SNS는
        // <head>에 분석/하이드레이션 스크립트가 많아 64KB로는 og:image 태그
        // 이전에 잘려나갈 수 있어, 여유를 두고 512KB로 잡는다.
        private const val MAX_BODY_SIZE_BYTES = 512 * 1024
    }

    suspend fun fetchOgImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null

        return withContext(ioDispatcher) {
            runCatching {
                val doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MILLIS)
                    .maxBodySize(MAX_BODY_SIZE_BYTES)
                    .get()

                doc.selectFirst("meta[property=og:image]")?.attr("content")?.ifBlank { null }
                    ?: doc.selectFirst("meta[name=twitter:image]")?.attr("content")?.ifBlank { null }
            }.onFailure {
                Log.e(TAG, "Failed to parse OG tag from $url: ${it.message}")
            }.getOrNull()
        }
    }
}

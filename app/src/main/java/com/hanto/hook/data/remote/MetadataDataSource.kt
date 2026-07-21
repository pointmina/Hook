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
    }

    suspend fun fetchOgImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null

        return withContext(ioDispatcher) {
            runCatching {
                val doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MILLIS)
                    .get()

                doc.selectFirst("meta[property=og:image]")?.attr("content")?.ifBlank { null }
                    ?: doc.selectFirst("meta[name=twitter:image]")?.attr("content")?.ifBlank { null }
            }.onFailure {
                Log.e(TAG, "Failed to parse OG tag from $url: ${it.message}")
            }.getOrNull()
        }
    }
}

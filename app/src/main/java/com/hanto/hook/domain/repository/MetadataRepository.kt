package com.hanto.hook.domain.repository

/**
 * 링크 메타데이터(썸네일 등) 조회 계약.
 *
 * Jsoup 등 스크래핑 세부사항은 데이터 레이어에 격리된다.
 */
interface MetadataRepository {

    /**
     * URL 페이지의 대표 이미지(og:image / twitter:image)를 조회한다.
     * @return 이미지 URL, 없거나 실패 시 null
     */
    suspend fun fetchOgImageUrl(url: String?): String?
}

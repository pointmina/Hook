package com.hanto.hook.domain.usecase

import android.util.Log
import com.hanto.hook.di.ApplicationScope
import com.hanto.hook.domain.repository.HookRepository
import com.hanto.hook.domain.repository.MetadataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 훅 저장/수정 흐름에서 공통으로 쓰는 썸네일 백그라운드 백필 로직.
 * AddHookUseCase와 UpdateHookUseCase가 동일한 크롤링/갱신 블록을 중복 구현하지
 * 않도록 한 곳에 모은다.
 */
class ThumbnailBackfiller @Inject constructor(
    private val hookRepository: HookRepository,
    private val metadataRepository: MetadataRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    companion object {
        private const val TAG = "ThumbnailBackfiller"
    }

    fun backfill(hookId: String, url: String?) {
        applicationScope.launch {
            try {
                val imageUrl = metadataRepository.fetchOgImageUrl(url)
                if (!imageUrl.isNullOrBlank()) {
                    hookRepository.updateHookImage(hookId, imageUrl)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to backfill thumbnail for $hookId", e)
            }
        }
    }
}

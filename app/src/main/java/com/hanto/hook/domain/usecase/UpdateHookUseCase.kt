package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import javax.inject.Inject

/**
 * 훅과 태그를 수정한다. 썸네일 크롤링은 수정 저장을 막지 않도록 백그라운드에서
 * 진행하고, 성공(null이 아님)했을 때만 기존 이미지를 새 이미지로 갱신한다.
 */
class UpdateHookUseCase @Inject constructor(
    private val hookRepository: HookRepository,
    private val thumbnailBackfiller: ThumbnailBackfiller
) {
    suspend operator fun invoke(hook: Hook, tags: List<String>) {
        hookRepository.updateHook(hook.copy(tags = tags))
        thumbnailBackfiller.backfill(hook.hookId, hook.url)
    }
}

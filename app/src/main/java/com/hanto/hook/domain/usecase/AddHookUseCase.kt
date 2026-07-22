package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import javax.inject.Inject

/**
 * 훅을 저장한다. 썸네일 크롤링은 저장을 막지 않도록 백그라운드에서 진행하고,
 * 완료되면 imageUrl만 갱신한다(Room Flow 구독으로 화면에 자동 반영).
 */
class AddHookUseCase @Inject constructor(
    private val hookRepository: HookRepository,
    private val thumbnailBackfiller: ThumbnailBackfiller
) {
    suspend operator fun invoke(hook: Hook, tags: List<String>) {
        hookRepository.addHook(hook.copy(tags = tags))
        thumbnailBackfiller.backfill(hook.hookId, hook.url)
    }
}

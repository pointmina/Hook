package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import com.hanto.hook.domain.repository.MetadataRepository
import javax.inject.Inject

/**
 * 훅과 태그를 수정한다. 썸네일 크롤링에 성공하면 이미지를 갱신하고,
 * 실패(null)하면 기존 이미지를 유지한다.
 */
class UpdateHookUseCase @Inject constructor(
    private val hookRepository: HookRepository,
    private val metadataRepository: MetadataRepository
) {
    suspend operator fun invoke(hook: Hook, tags: List<String>) {
        val imageUrl = metadataRepository.fetchOgImageUrl(hook.url)
        val finalHook = if (!imageUrl.isNullOrBlank()) {
            hook.copy(imageUrl = imageUrl, tags = tags)
        } else {
            hook.copy(tags = tags)
        }
        hookRepository.updateHook(finalHook)
    }
}

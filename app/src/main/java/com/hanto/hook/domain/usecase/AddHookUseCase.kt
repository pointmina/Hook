package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import com.hanto.hook.domain.repository.MetadataRepository
import javax.inject.Inject

/**
 * 훅을 저장한다. 저장 전에 링크의 썸네일을 크롤링해 함께 붙인다.
 * (썸네일 크롤링 + 저장의 조합이 이 앱의 핵심 비즈니스 규칙)
 */
class AddHookUseCase @Inject constructor(
    private val hookRepository: HookRepository,
    private val metadataRepository: MetadataRepository
) {
    suspend operator fun invoke(hook: Hook, tags: List<String>) {
        val imageUrl = metadataRepository.fetchOgImageUrl(hook.url)
        hookRepository.addHook(hook.copy(imageUrl = imageUrl, tags = tags))
    }
}

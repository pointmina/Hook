package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import javax.inject.Inject

/**
 * 특정 이름의 태그를 모두 삭제한다.
 */
class DeleteTagUseCase @Inject constructor(
    private val repository: HookRepository
) {
    suspend operator fun invoke(tagName: String) = repository.deleteTag(tagName)
}

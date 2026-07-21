package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 특정 훅에 달린 태그 이름 목록을 관찰한다.
 */
class ObserveHookTagsUseCase @Inject constructor(
    private val repository: HookRepository
) {
    operator fun invoke(hookId: String): Flow<List<String>> =
        repository.observeTagsForHook(hookId)
}

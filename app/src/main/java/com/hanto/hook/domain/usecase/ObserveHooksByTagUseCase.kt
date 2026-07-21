package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.model.TAG_UNCATEGORIZED
import com.hanto.hook.domain.repository.HookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 선택된 태그에 해당하는 훅 리스트를 관찰한다.
 * - null/blank: 빈 리스트
 * - '미분류': 태그가 없는 훅
 * - 그 외: 해당 태그가 달린 훅
 */
class ObserveHooksByTagUseCase @Inject constructor(
    private val repository: HookRepository
) {
    operator fun invoke(tagName: String?): Flow<List<Hook>> = when {
        tagName.isNullOrBlank() -> kotlinx.coroutines.flow.flowOf(emptyList())
        tagName == TAG_UNCATEGORIZED -> repository.observeHooksWithoutTags()
        else -> repository.observeHooksByTag(tagName)
    }
}

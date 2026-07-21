package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import com.hanto.hook.util.SoundSearcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * 검색어에 따라 훅 리스트를 필터링한다.
 * 검색어가 비면 전체를, 아니면 제목/설명에 대해 초성 검색으로 필터링한다.
 */
class SearchHooksUseCase @Inject constructor(
    private val repository: HookRepository
) {
    operator fun invoke(query: Flow<String>): Flow<List<Hook>> =
        combine(repository.observeHooks(), query) { hooks, keyword ->
            if (keyword.isBlank()) {
                hooks
            } else {
                hooks.filter { hook ->
                    SoundSearcher.matchString(hook.title, keyword) ||
                        (hook.description?.let { SoundSearcher.matchString(it, keyword) } ?: false)
                }
            }
        }
}

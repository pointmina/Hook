package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 중복 제거 후 정렬된 태그 이름 목록을 관찰한다.
 */
class ObserveTagNamesUseCase @Inject constructor(
    private val repository: HookRepository
) {
    operator fun invoke(): Flow<List<String>> =
        repository.observeTagNames().map { names -> names.distinct().sorted() }
}

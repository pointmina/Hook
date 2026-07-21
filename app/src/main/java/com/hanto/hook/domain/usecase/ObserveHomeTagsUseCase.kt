package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.TAG_UNCATEGORIZED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 홈/태그 화면에 노출할 태그 목록. 맨 앞에 '미분류'를 붙인다.
 */
class ObserveHomeTagsUseCase @Inject constructor(
    private val observeTagNames: ObserveTagNamesUseCase
) {
    operator fun invoke(): Flow<List<String>> =
        observeTagNames().map { tags -> listOf(TAG_UNCATEGORIZED) + tags }
}

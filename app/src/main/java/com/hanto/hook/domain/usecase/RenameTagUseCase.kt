package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import javax.inject.Inject

/**
 * 태그 이름을 일괄 변경한다.
 */
class RenameTagUseCase @Inject constructor(
    private val repository: HookRepository
) {
    suspend operator fun invoke(oldName: String, newName: String) =
        repository.renameTag(oldName, newName)
}

package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import javax.inject.Inject

/**
 * 훅과 그에 딸린 태그를 함께 삭제한다.
 */
class DeleteHookUseCase @Inject constructor(
    private val repository: HookRepository
) {
    suspend operator fun invoke(hookId: String) = repository.deleteHook(hookId)
}

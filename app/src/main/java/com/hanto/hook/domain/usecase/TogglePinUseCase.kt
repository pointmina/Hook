package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import javax.inject.Inject

/**
 * 훅의 고정(핀) 상태를 변경한다.
 */
class TogglePinUseCase @Inject constructor(
    private val repository: HookRepository
) {
    suspend operator fun invoke(hookId: String, isPinned: Boolean) =
        repository.setPinned(hookId, isPinned)
}

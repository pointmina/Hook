package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TogglePinUseCaseTest {

    private val repository: HookRepository = mockk(relaxed = true)
    private val togglePin = TogglePinUseCase(repository)

    @Test
    fun `고정 상태 변경을 저장소에 위임한다`() = runTest {
        togglePin("hook-1", true)

        coVerify { repository.setPinned("hook-1", true) }
    }
}

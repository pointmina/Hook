package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteHookUseCaseTest {

    private val repository: HookRepository = mockk(relaxed = true)
    private val deleteHook = DeleteHookUseCase(repository)

    @Test
    fun `저장소의 삭제를 그대로 위임한다`() = runTest {
        deleteHook("hook-1")

        coVerify { repository.deleteHook("hook-1") }
    }
}

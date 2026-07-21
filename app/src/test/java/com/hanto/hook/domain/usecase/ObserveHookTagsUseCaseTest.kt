package com.hanto.hook.domain.usecase

import app.cash.turbine.test
import com.hanto.hook.domain.repository.HookRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveHookTagsUseCaseTest {

    private val repository: HookRepository = mockk()
    private val observeHookTags = ObserveHookTagsUseCase(repository)

    @Test
    fun `저장소가 관찰하는 태그 목록을 그대로 내보낸다`() = runTest {
        every { repository.observeTagsForHook("hook-1") } returns flowOf(listOf("kotlin"))

        observeHookTags("hook-1").test {
            assertEquals(listOf("kotlin"), awaitItem())
            awaitComplete()
        }
    }
}

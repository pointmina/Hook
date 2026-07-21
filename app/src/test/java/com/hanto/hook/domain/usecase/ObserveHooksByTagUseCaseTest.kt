package com.hanto.hook.domain.usecase

import app.cash.turbine.test
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.model.TAG_UNCATEGORIZED
import com.hanto.hook.domain.repository.HookRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveHooksByTagUseCaseTest {

    private val repository: HookRepository = mockk()
    private val observeHooksByTag = ObserveHooksByTagUseCase(repository)

    private fun hook(id: String) = Hook(hookId = id, title = id, url = null, description = null)

    @Test
    fun `태그가 null이면 빈 리스트를 내보낸다`() = runTest {
        observeHooksByTag(null).test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `태그가 공백이면 빈 리스트를 내보낸다`() = runTest {
        observeHooksByTag("   ").test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `미분류면 태그 없는 훅 목록을 관찰한다`() = runTest {
        every { repository.observeHooksWithoutTags() } returns flowOf(listOf(hook("1")))

        observeHooksByTag(TAG_UNCATEGORIZED).test {
            assertEquals(listOf(hook("1")), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `일반 태그면 해당 태그의 훅 목록을 관찰한다`() = runTest {
        every { repository.observeHooksByTag("kotlin") } returns flowOf(listOf(hook("2")))

        observeHooksByTag("kotlin").test {
            assertEquals(listOf(hook("2")), awaitItem())
            awaitComplete()
        }
    }
}

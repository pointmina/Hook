package com.hanto.hook.domain.usecase

import app.cash.turbine.test
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchHooksUseCaseTest {

    private val repository: HookRepository = mockk()
    private val searchHooks = SearchHooksUseCase(repository)

    private val hooks = listOf(
        Hook(hookId = "1", title = "안드로이드 공부", url = null, description = null),
        Hook(hookId = "2", title = "iOS 공부", url = null, description = "스위프트 정리")
    )

    @Test
    fun `검색어가 비어있으면 전체를 내보낸다`() = runTest {
        every { repository.observeHooks() } returns flowOf(hooks)

        searchHooks(flowOf("")).test {
            assertEquals(hooks, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `검색어에 매칭하는 제목만 걸러낸다`() = runTest {
        every { repository.observeHooks() } returns flowOf(hooks)

        searchHooks(flowOf("안드로이드")).test {
            assertEquals(listOf(hooks[0]), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `제목에 없어도 설명에 매칭하면 포함한다`() = runTest {
        every { repository.observeHooks() } returns flowOf(hooks)

        searchHooks(flowOf("스위프트")).test {
            assertEquals(listOf(hooks[1]), awaitItem())
            awaitComplete()
        }
    }
}

package com.hanto.hook.domain.usecase

import app.cash.turbine.test
import com.hanto.hook.domain.repository.HookRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveTagNamesUseCaseTest {

    private val repository: HookRepository = mockk()
    private val observeTagNames = ObserveTagNamesUseCase(repository)

    @Test
    fun `중복을 제거하고 정렬해서 내보낸다`() = runTest {
        every { repository.observeTagNames() } returns
            flowOf(listOf("kotlin", "android", "kotlin", "AI"))

        observeTagNames().test {
            assertEquals(listOf("AI", "android", "kotlin"), awaitItem())
            awaitComplete()
        }
    }
}

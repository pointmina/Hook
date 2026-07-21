package com.hanto.hook.domain.usecase

import app.cash.turbine.test
import com.hanto.hook.domain.model.TAG_UNCATEGORIZED
import com.hanto.hook.domain.repository.HookRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveHomeTagsUseCaseTest {

    private val repository: HookRepository = mockk()
    private val observeHomeTags = ObserveHomeTagsUseCase(ObserveTagNamesUseCase(repository))

    @Test
    fun `미분류 태그를 맨 앞에 붙인다`() = runTest {
        every { repository.observeTagNames() } returns flowOf(listOf("kotlin", "android"))

        observeHomeTags().test {
            assertEquals(listOf(TAG_UNCATEGORIZED, "android", "kotlin"), awaitItem())
            awaitComplete()
        }
    }
}

package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddHookUseCaseTest {

    private val hookRepository: HookRepository = mockk(relaxed = true)
    private val thumbnailBackfiller: ThumbnailBackfiller = mockk(relaxed = true)
    private lateinit var addHook: AddHookUseCase

    private val hook = Hook(hookId = "1", title = "title", url = "https://example.com", description = null)

    @Before
    fun setUp() {
        addHook = AddHookUseCase(hookRepository, thumbnailBackfiller)
    }

    @Test
    fun `태그를 붙여 저장하고 썸네일 백필을 트리거한다`() = runTest {
        addHook(hook, listOf("kotlin", "android"))

        coVerify {
            hookRepository.addHook(hook.copy(tags = listOf("kotlin", "android")))
        }
        verify { thumbnailBackfiller.backfill(hook.hookId, hook.url) }
    }
}

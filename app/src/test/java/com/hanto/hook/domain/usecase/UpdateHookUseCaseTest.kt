package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateHookUseCaseTest {

    private val hookRepository: HookRepository = mockk(relaxed = true)
    private val thumbnailBackfiller: ThumbnailBackfiller = mockk(relaxed = true)
    private lateinit var updateHook: UpdateHookUseCase

    private val hook = Hook(
        hookId = "1",
        title = "title",
        url = "https://example.com",
        description = null,
        imageUrl = "https://old-image.com/a.png"
    )

    @Before
    fun setUp() {
        updateHook = UpdateHookUseCase(hookRepository, thumbnailBackfiller)
    }

    @Test
    fun `태그를 붙여 수정하고 썸네일 백필을 트리거한다`() = runTest {
        updateHook(hook, listOf("tag1"))

        coVerify {
            hookRepository.updateHook(hook.copy(tags = listOf("tag1")))
        }
        verify { thumbnailBackfiller.backfill(hook.hookId, hook.url) }
    }
}

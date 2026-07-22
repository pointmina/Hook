package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import com.hanto.hook.domain.repository.MetadataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateHookUseCaseTest {

    private val hookRepository: HookRepository = mockk(relaxed = true)
    private val metadataRepository: MetadataRepository = mockk()
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
        updateHook = UpdateHookUseCase(hookRepository, metadataRepository)
    }

    @Test
    fun `새 썸네일 크롤링에 성공하면 이미지를 갱신한다`() = runTest {
        coEvery { metadataRepository.fetchOgImageUrl(hook.url) } returns "https://new-image.com/b.png"

        updateHook(hook, listOf("tag1"))

        coVerify {
            hookRepository.updateHook(
                hook.copy(imageUrl = "https://new-image.com/b.png", tags = listOf("tag1"))
            )
        }
    }

    @Test
    fun `썸네일 크롤링에 실패하면 기존 이미지를 유지한다`() = runTest {
        coEvery { metadataRepository.fetchOgImageUrl(hook.url) } returns null

        updateHook(hook, listOf("tag1"))

        coVerify { hookRepository.updateHook(hook.copy(tags = listOf("tag1"))) }
    }

    @Test
    fun `썸네일 크롤링 결과가 빈 문자열이면 기존 이미지를 유지한다`() = runTest {
        coEvery { metadataRepository.fetchOgImageUrl(hook.url) } returns ""

        updateHook(hook, listOf("tag1"))

        coVerify { hookRepository.updateHook(hook.copy(tags = listOf("tag1"))) }
    }
}

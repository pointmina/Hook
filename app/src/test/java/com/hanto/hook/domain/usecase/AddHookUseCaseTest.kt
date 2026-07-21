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

class AddHookUseCaseTest {

    private val hookRepository: HookRepository = mockk(relaxed = true)
    private val metadataRepository: MetadataRepository = mockk()
    private lateinit var addHook: AddHookUseCase

    private val hook = Hook(hookId = "1", title = "title", url = "https://example.com", description = null)

    @Before
    fun setUp() {
        addHook = AddHookUseCase(hookRepository, metadataRepository)
    }

    @Test
    fun `크롤링한 썸네일과 태그를 붙여 저장한다`() = runTest {
        coEvery { metadataRepository.fetchOgImageUrl(hook.url) } returns "https://image.com/a.png"

        addHook(hook, listOf("kotlin", "android"))

        coVerify {
            hookRepository.addHook(
                hook.copy(imageUrl = "https://image.com/a.png", tags = listOf("kotlin", "android"))
            )
        }
    }

    @Test
    fun `썸네일 크롤링에 실패하면 이미지 없이 저장한다`() = runTest {
        coEvery { metadataRepository.fetchOgImageUrl(hook.url) } returns null

        addHook(hook, emptyList())

        coVerify { hookRepository.addHook(hook.copy(imageUrl = null, tags = emptyList())) }
    }
}

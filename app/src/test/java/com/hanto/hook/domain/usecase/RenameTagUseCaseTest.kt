package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RenameTagUseCaseTest {

    private val repository: HookRepository = mockk(relaxed = true)
    private val renameTag = RenameTagUseCase(repository)

    @Test
    fun `태그 이름 변경을 저장소에 위임한다`() = runTest {
        renameTag("old", "new")

        coVerify { repository.renameTag("old", "new") }
    }
}

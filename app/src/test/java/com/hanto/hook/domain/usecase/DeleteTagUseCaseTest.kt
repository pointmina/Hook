package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.repository.HookRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteTagUseCaseTest {

    private val repository: HookRepository = mockk(relaxed = true)
    private val deleteTag = DeleteTagUseCase(repository)

    @Test
    fun `태그 삭제를 저장소에 위임한다`() = runTest {
        deleteTag("kotlin")

        coVerify { repository.deleteTag("kotlin") }
    }
}

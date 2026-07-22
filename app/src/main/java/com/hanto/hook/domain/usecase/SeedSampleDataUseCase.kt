package com.hanto.hook.domain.usecase

import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import java.util.UUID
import javax.inject.Inject

/**
 * 디버그 빌드에서 DB가 비어있을 때 샘플 데이터를 채운다.
 * DAO를 직접 건드리지 않고 AddHookUseCase를 통해 저장해, 저장 경로(에러 로깅,
 * 썸네일 백필 등)를 프로덕션 저장 흐름과 동일하게 유지한다.
 */
class SeedSampleDataUseCase @Inject constructor(
    private val hookRepository: HookRepository,
    private val addHook: AddHookUseCase
) {
    private val samples = listOf(
        Triple(
            "Jetpack Compose 공식 문서",
            "https://developer.android.com/jetpack/compose",
            "안드로이드 선언형 UI 툴킷 공식 가이드"
        ) to listOf("Android", "Compose"),
        Triple(
            "Kotlin Coroutines 가이드",
            "https://kotlinlang.org/docs/coroutines-guide.html",
            "코루틴 개념과 사용법 정리"
        ) to listOf("Kotlin", "Coroutine"),
        Triple(
            "Effective Kotlin",
            "https://kt.academy/book/effectivekotlin",
            "코틀린을 더 잘 쓰기 위한 책"
        ) to listOf("Kotlin", "Book"),
        Triple(
            "Room 공식 문서",
            "https://developer.android.com/training/data-storage/room",
            "안드로이드 로컬 데이터베이스 라이브러리"
        ) to listOf("Android", "Database"),
        Triple(
            "인스타그램",
            "https://www.instagram.com/",
            "커스텀 스킴 딥링크 테스트용 샘플"
        ) to listOf("SNS", "Instagram"),
        Triple(
            "쓰레드 게시물",
            "https://www.threads.com/@sugarsolsol_/post/DbEh96rk4dg",
            "커스텀 스킴 딥링크 테스트용 샘플"
        ) to listOf("SNS", "Threads"),
        Triple(
            "네이버 블로그",
            "https://blog.naver.com/",
            "커스텀 스킴 딥링크 테스트용 샘플"
        ) to listOf("Blog", "Naver"),
        Triple(
            "쿠팡",
            "https://www.coupang.com/",
            "커스텀 스킴 딥링크 테스트용 샘플"
        ) to listOf("Shopping", "Coupang"),
    )

    suspend operator fun invoke() {
        if (hookRepository.hasAnyHook()) return

        samples.forEachIndexed { index, (info, tagNames) ->
            val (title, url, description) = info
            val hook = Hook(
                hookId = UUID.randomUUID().toString(),
                title = title,
                url = url,
                description = description,
                isPinned = index == 0
            )
            addHook(hook, tagNames)
        }
    }
}

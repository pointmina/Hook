package com.hanto.hook.domain.repository

import com.hanto.hook.domain.model.Hook
import kotlinx.coroutines.flow.Flow

/**
 * 훅/태그 저장소 계약.
 *
 * 도메인 레이어가 소유하는 인터페이스로, 데이터 레이어가 이를 구현한다(의존성 역전).
 * 모든 쓰기 작업은 실패 시 예외를 던지는 것을 계약으로 한다.
 */
interface HookRepository {

    // ---------------------- 조회 ---------------------- //
    fun observeHooks(): Flow<List<Hook>>
    fun observeHooksByTag(tagName: String): Flow<List<Hook>>
    fun observeHooksWithoutTags(): Flow<List<Hook>>
    fun observeTagNames(): Flow<List<String>>
    fun observeTagsForHook(hookId: String): Flow<List<String>>

    // ---------------------- 쓰기 ---------------------- //
    suspend fun addHook(hook: Hook)
    suspend fun updateHook(hook: Hook)
    suspend fun deleteHook(hookId: String)
    suspend fun setPinned(hookId: String, isPinned: Boolean)
    suspend fun renameTag(oldName: String, newName: String)
    suspend fun deleteTag(tagName: String)
}

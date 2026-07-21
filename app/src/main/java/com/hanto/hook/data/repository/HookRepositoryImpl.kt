package com.hanto.hook.data.repository

import android.util.Log
import com.hanto.hook.data.dao.HookDao
import com.hanto.hook.data.mapper.toDomain
import com.hanto.hook.data.mapper.toEntity
import com.hanto.hook.data.mapper.toTagEntities
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.repository.HookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HookRepositoryImpl @Inject constructor(
    private val hookDao: HookDao
) : HookRepository {

    companion object {
        private const val TAG = "HookRepositoryImpl"
    }

    // ---------------------- 조회 ---------------------- //

    override fun observeHooks(): Flow<List<Hook>> =
        hookDao.getHooksWithTags().map { it.toDomain() }

    override fun observeHooksByTag(tagName: String): Flow<List<Hook>> =
        hookDao.getHooksByTagName(tagName).map { it.toDomain() }

    override fun observeHooksWithoutTags(): Flow<List<Hook>> =
        hookDao.getHooksWithNoTags().map { it.toDomain() }

    override fun observeTagNames(): Flow<List<String>> =
        hookDao.getAllTagNames()

    override fun observeTagsForHook(hookId: String): Flow<List<String>> =
        hookDao.getTagNamesForHook(hookId)

    // ---------------------- 쓰기 ---------------------- //

    override suspend fun addHook(hook: Hook) {
        try {
            hookDao.insertHookWithTags(hook.toEntity(), hook.toTagEntities())
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting hook: ${hook.title}", e)
            throw e
        }
    }

    override suspend fun updateHook(hook: Hook) {
        try {
            hookDao.updateHookWithTags(hook.toEntity(), hook.toTagEntities())
        } catch (e: Exception) {
            Log.e(TAG, "Error updating hook: ${hook.hookId}", e)
            throw e
        }
    }

    override suspend fun deleteHook(hookId: String) {
        try {
            hookDao.deleteHookAndTags(hookId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting hook: $hookId", e)
            throw e
        }
    }

    override suspend fun setPinned(hookId: String, isPinned: Boolean) {
        try {
            hookDao.updatePinStatus(hookId, isPinned)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating pin status: $hookId", e)
            throw e
        }
    }

    override suspend fun renameTag(oldName: String, newName: String) {
        try {
            hookDao.updateTagName(oldName, newName)
        } catch (e: Exception) {
            Log.e(TAG, "Error renaming tag: $oldName -> $newName", e)
            throw e
        }
    }

    override suspend fun deleteTag(tagName: String) {
        try {
            hookDao.deleteTagByTagName(tagName)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting tag: $tagName", e)
            throw e
        }
    }
}

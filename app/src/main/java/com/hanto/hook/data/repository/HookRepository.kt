package com.hanto.hook.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.hanto.hook.data.dao.HookDao
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HookRepository @Inject constructor(
    private val hookDao: HookDao
) {

    companion object {
        private const val TAG = "HookRepository"
    }

    // ---------------------- 데이터 삽입---------------------- //

    suspend fun insertHook(hook: Hook): Long = withContext(Dispatchers.IO) {
        try {
            hookDao.insertHook(hook)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting hook: ${hook.title}", e)
            throw e
        }
    }

    suspend fun insertTag(tag: Tag): Long = withContext(Dispatchers.IO) {
        try {
            hookDao.insertTag(tag)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting tag: ${tag.name}", e)
            throw e
        }
    }

    // ---------------------- 데이터 삭제---------------------- //

    suspend fun deleteHook(hookId: String) = withContext(Dispatchers.IO) {
        try {
            hookDao.deleteHookById(hookId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting hook: $hookId", e)
            throw e
        }
    }

    suspend fun deleteTagByHookId(hookId: String) = withContext(Dispatchers.IO) {
        try {
            hookDao.deleteTagByHookId(hookId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting tags for hook: $hookId", e)
            throw e
        }
    }

    suspend fun deleteTagByTagName(tagName: String) = withContext(Dispatchers.IO) {
        try {
            hookDao.deleteTagByTagName(tagName)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting tag: $tagName", e)
            throw e
        }
    }

    suspend fun deleteHookAndTags(hookId: String) = withContext(Dispatchers.IO) {
        try {
            hookDao.deleteHookAndTags(hookId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting hook and tags: $hookId", e)
            throw e
        }
    }

    // ---------------------- 데이터 업데이트---------------------- //

    suspend fun updateHook(hook: Hook) = withContext(Dispatchers.IO) {
        try {
            hookDao.updateHook(hook)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating hook: ${hook.hookId}", e)
            throw e
        }
    }

    private suspend fun updateTagsForHook(hookId: String, selectedTags: List<String>) =
        withContext(Dispatchers.IO) {
            try {
                // 기존 태그들 삭제
                hookDao.deleteTagByHookId(hookId)

                // 새로운 태그들 삽입
                selectedTags.forEach { tagName ->
                    val tag = Tag(hookId = hookId, name = tagName)
                    hookDao.insertTag(tag)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating tags for hook: $hookId", e)
                throw e
            }
        }

    suspend fun updateHookAndTags(hook: Hook, selectedTags: List<String>) =
        withContext(Dispatchers.IO) {
            try {
                hookDao.updateHook(hook)
                updateTagsForHook(hook.hookId, selectedTags)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating hook and tags: ${hook.hookId}", e)
                throw e
            }
        }

    suspend fun updateTagName(oldTagName: String, newTagName: String) =
        withContext(Dispatchers.IO) {
            try {
                hookDao.updateTagName(oldTagName, newTagName)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating tag name: $oldTagName -> $newTagName", e)
                throw e
            }
        }

    suspend fun setPinned(hookId: String, isPinned: Boolean) = withContext(Dispatchers.IO) {
        try {
            hookDao.updatePinStatus(hookId, isPinned)
        } catch (e: Exception) {
            Log.e(TAG, "Repository: Error updating pin status: $hookId", e)
        }
    }

    // ---------------------- 데이터 조회---------------------- //

    fun getAllHooks(): LiveData<List<Hook>> = hookDao.getAllHooks()

    fun getTagsForHook(hookId: String): LiveData<List<Tag>> = hookDao.getTagsForHook(hookId)

    fun getAllTagNames(): LiveData<List<String>> = hookDao.getAllTagNames()

    fun getHooksByTagName(tagName: String): LiveData<List<Hook>> =
        hookDao.getHooksByTagName(tagName)
}
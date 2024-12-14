package com.hanto.hook.repository

import com.hanto.hook.MainApplication
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookTagMapping
import com.hanto.hook.data.Tag

class HookRepository {
    private val hookDao = MainApplication.getDatabase().hookDao()
    private val tagDao = MainApplication.getDatabase().tagDao()
    private val hookTagMappingDao = MainApplication.getDatabase().hookTagMappingDao()

    suspend fun insertHookWithTags(hook: Hook, tags: List<String>) {
        val hookId = hookDao.insertHook(hook).toInt()

        for (tagName in tags) {
            val tagId = tagDao.insertTag(Tag(name = tagName)).toInt()
            hookTagMappingDao.insertMapping(HookTagMapping(hookId = hookId, tagId = tagId))
        }
    }

    suspend fun getHooksByTag(tagName: String): List<Hook> {
        return hookTagMappingDao.getHooksByTag(tagName)
    }

    suspend fun getTagsByHook(hookId: Int): List<Tag> {
        return hookTagMappingDao.getTagsByHook(hookId)
    }
}

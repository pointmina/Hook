package com.hanto.hook.viewmodel

import HookRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.HookTagMapping
import com.hanto.hook.data.model.Tag
import com.hanto.hook.database.DatabaseModule

class HookViewModel : ViewModel() {

    private var liveDataHook: LiveData<List<Hook>>? = null
    private var liveDataTag: LiveData<List<Tag>>? = null
    private var liveDataTagName: LiveData<List<String>>? = null
    private var liveDataHookByTagName: LiveData<List<Hook>>? = null
    private var appDatabase = DatabaseModule.getDatabase()
    private var hookRepository =  HookRepository.getInstance(appDatabase)

    fun insertHook(hook: Hook) {
        hookRepository.insertHook(hook)
    }

    fun insertTag(tag: Tag) {
        hookRepository.insertTag(tag)
    }

    fun insertMapping(hookTag: HookTagMapping) {
        hookRepository.insertMapping(hookTag)
    }

    fun deleteHook(hookId: String) {
        hookRepository.deleteHook(hookId)
    }

    fun deleteTagByHookId(hookId: Long) {
        hookRepository.deleteTagByHookId(hookId)
    }

    fun deleteMappingsByHookId(hookId: Long) {
        hookRepository.deleteMappingsByHookId(hookId)
    }

    fun deleteHookAndTags(hookId: Long) {
        hookRepository.deleteHookAndTags(hookId)
    }

    fun getAllHooks(): LiveData<List<Hook>>? {
        hookRepository.getAllHooks()
        return liveDataHook
    }

    fun getTagsForHook(hookId: Long): LiveData<List<Tag>>? {
        hookRepository.getTagsForHook(hookId)
        return liveDataTag
    }

    fun getAllTagNames(): LiveData<List<String>>? {
        hookRepository.getAllTagNames()
        return liveDataTagName
    }

    fun getHookByTag(hookId: Long): LiveData<List<Hook>>? {
        hookRepository.getHookByTag(hookId)
        return liveDataHookByTagName
    }

}

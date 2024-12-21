package com.hanto.hook.viewmodel

import HookRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.HookTagMapping
import com.hanto.hook.data.model.Tag
import com.hanto.hook.database.DatabaseModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//viewModel <-> UI
class HookViewModel : ViewModel() {

    private val appDatabase = DatabaseModule.getDatabase()
    private val hookRepository = HookRepository.getInstance(appDatabase)

    // LiveData 필드
    val liveDataHook: LiveData<List<Hook>> = hookRepository.getAllHooks()
    val liveDataTagName: LiveData<List<String>>? = null
    val liveDataHookByTagName: LiveData<List<Hook>>? = null

    // 데이터 삽입 메서드
    fun insertHook(hook: Hook) {
        hookRepository.insertHook(hook)
    }

    fun insertTag(tag: Tag) {
        hookRepository.insertTag(tag)
    }

    fun insertMapping(hookTag: HookTagMapping) {
        hookRepository.insertMapping(hookTag)
    }

    // 데이터 삭제 메서드
    fun deleteHook(hookId: String) {
        hookRepository.deleteHook(hookId)
    }

    fun deleteTagByHookId(hookId: String) {
        hookRepository.deleteTagByHookId(hookId)
    }

    fun deleteMappingsByHookId(hookId: String) {
        hookRepository.deleteMappingsByHookId(hookId)
    }

//
//    fun deleteHookAndTags(hookId: String) {
//        hookRepository.deleteHookAndTags(hookId)
//    }

    fun deleteHookAndTags(hookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hookRepository.deleteHookAndTags(hookId)
            } catch (e: Exception) {
                // 예외 처리
                Log.e("HookViewModel", "Error deleting hook and tags", e)
            }
        }
    }

    //데이터 업데이트 메서드
    fun updateHookAndTags(hook: Hook, selectedTags: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hookRepository.updateHookAndTags(hook, selectedTags)
            } catch (e: Exception) {
                // 예외 처리
                Log.e("HookViewModel", "Error updating hook and tags", e)
            }
        }
    }


    // 데이터 조회 메서드
    fun getAllHooks(): LiveData<List<Hook>> {
        return liveDataHook
    }

    fun getTagsForHook(hookId: String): LiveData<List<Tag>>? {
        return hookRepository.getTagsForHook(hookId)
    }

    fun getAllTagNames(): LiveData<List<String>> {
        return hookRepository.getAllTagNames() // Repository에서 직접 반환
    }

    fun getHookByTag(tagName: String): LiveData<List<Hook>?> {
        return hookRepository.getHookByTag(tagName) // Repository에서 직접 반환
    }
}
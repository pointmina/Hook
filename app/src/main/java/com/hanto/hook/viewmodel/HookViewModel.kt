package com.hanto.hook.viewmodel

import HookRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.hanto.hook.data.model.Hook
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

    private var liveDataTagName: LiveData<List<String>> = hookRepository.getAllTagNames()

    var distinctTagNames: LiveData<List<String>> = liveDataTagName.map { tagNames ->
        tagNames.distinct()
    }

    private val _selectedTagName = MutableLiveData<String>()
    val selectedTagName: LiveData<String> get() = _selectedTagName


    // 데이터 삽입 메서드
    fun insertHook(hook: Hook) {
        hookRepository.insertHook(hook)
    }

    fun insertTag(tag: Tag) {
        hookRepository.insertTag(tag)
    }


    // 데이터 삭제 메서드
    fun deleteHook(hookId: String) {
        hookRepository.deleteHook(hookId)
    }

    fun deleteTagByHookId(hookId: String) {
        hookRepository.deleteTagByHookId(hookId)
    }


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
                Log.e("HookViewModel", "Error updating hook and tags", e)
            }
        }
    }

    fun updateTagName(oldTagName: String, newTagName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hookRepository.updateTagName(oldTagName, newTagName)

            } catch (e: Exception) {
                Log.e("HookViewModel", "Error updating tag name", e)
            }
        }
    }

    private val hooksLiveDataCache = MutableLiveData<String>()

    fun getHooksByTagName(tagName: String): LiveData<List<Hook>?> {
        if (hooksLiveDataCache.value == tagName) {
            return hookRepository.getHooksByTagName(tagName)
        }

        hooksLiveDataCache.value = tagName
        return hookRepository.getHooksByTagName(tagName)
    }


    // 데이터 조회 메서드
    fun getAllHooks(): LiveData<List<Hook>> {
        return liveDataHook
    }

    fun getTagsForHook(hookId: String): LiveData<List<Tag>>? {
        return hookRepository.getTagsForHook(hookId)
    }

    fun getAllTagNames(): LiveData<List<String>> {
        return hookRepository.getAllTagNames()
    }


    fun setPinned(hookId: String, isPinned: Boolean) {
        viewModelScope.launch {
            hookRepository.setPinned(hookId, isPinned)
        }
    }

}
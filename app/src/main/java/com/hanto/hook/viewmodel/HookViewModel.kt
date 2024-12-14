package com.hanto.hook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.MainApplication
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookTagMapping
import com.hanto.hook.data.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HookViewModel : ViewModel() {

    // DAO 참조 (MainApplication을 통해 DB 가져오기)
    private val hookDao = MainApplication.getDatabase().hookDao()
    private val tagDao = MainApplication.getDatabase().tagDao()
    private val hookTagMappingDao = MainApplication.getDatabase().hookTagMappingDao()

    // LiveData를 통해 Hook 목록을 관찰
    private val _hooks = MutableLiveData<List<Hook>>()
    val hooks: LiveData<List<Hook>> get() = _hooks

    // LiveData를 통해 Tag 목록을 관찰
    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> get() = _tags

    // 특정 태그를 기반으로 Hook 검색
    private val _hooksByTag = MutableLiveData<List<Hook>>()
    val hooksByTag: LiveData<List<Hook>> get() = _hooksByTag

    // Hook 추가 메서드
    fun addHookWithTags(hook: Hook, tags: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val hookId = hookDao.insertHook(hook).toInt()

            for (tagName in tags) {
                val tagId = tagDao.insertTag(Tag(name = tagName)).toInt()
                hookTagMappingDao.insertMapping(HookTagMapping(hookId = hookId, tagId = tagId))
            }
            loadHooks()
        }
    }

    // 모든 Hook 로드
    private fun loadHooks() {
        viewModelScope.launch(Dispatchers.IO) {
            _hooks.postValue(hookDao.getAllHooks())
        }
    }

    // 모든 Tag 로드
    fun loadTags() {
        viewModelScope.launch(Dispatchers.IO) {
            _tags.postValue(tagDao.getAllTags())
        }
    }

    // 특정 태그 기반으로 Hook 검색
    fun searchHooksByTag(tagName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _hooksByTag.postValue(hookTagMappingDao.getHooksByTag(tagName))
        }
    }
}

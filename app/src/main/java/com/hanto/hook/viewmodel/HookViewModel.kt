package com.hanto.hook.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.data.repository.HookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HookViewModel @Inject constructor(
    private val hookRepository: HookRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HookViewModel"
    }

    // ---------------------- LiveData---------------------- //

    val hooks: LiveData<List<Hook>> = hookRepository.getAllHooks()
    private val tagNames: LiveData<List<String>> = hookRepository.getAllTagNames()

    // 중복 제거된 태그 이름들
    val distinctTagNames: LiveData<List<String>> = tagNames.map { tagNamesList ->
        tagNamesList.distinct().sorted()
    }

    // 선택된 태그명 관리
    private val _selectedTagName = MutableLiveData<String?>()
    val selectedTagName: LiveData<String?> = _selectedTagName

    val hooksBySelectedTag: LiveData<List<Hook>> = _selectedTagName.switchMap { tagName ->
        if (tagName.isNullOrBlank()) {
            MutableLiveData(emptyList())
        } else {
            hookRepository.getHooksByTagName(tagName)
        }
    }

    // 로딩 상태 관리
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 에러 상태 관리
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // ---------------------- 데이터 삽입---------------------- //

    fun insertHook(hook: Hook) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                hookRepository.insertHook(hook)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert hook: ${hook.title}", e)
                _errorMessage.value = "훅 저장 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun insertTag(tag: Tag) {
        viewModelScope.launch {
            try {
                hookRepository.insertTag(tag)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert tag: ${tag.name}", e)
                _errorMessage.value = "태그 저장 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }

    fun insertHookWithTags(hook: Hook, tags: List<String>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 훅 먼저 삽입
                hookRepository.insertHook(hook)

                // 태그들 삽입
                tags.forEach { tagName ->
                    val tag = Tag(hookId = hook.hookId, name = tagName)
                    hookRepository.insertTag(tag)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert hook with tags: ${hook.title}", e)
                _errorMessage.value = "훅과 태그 저장 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------------------- 데이터 삭제---------------------- //

    fun deleteHook(hookId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                hookRepository.deleteHook(hookId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete hook: $hookId", e)
                _errorMessage.value = "훅 삭제 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTagByHookId(hookId: String) {
        viewModelScope.launch {
            try {
                hookRepository.deleteTagByHookId(hookId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete tags for hook: $hookId", e)
                _errorMessage.value = "태그 삭제 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }

    fun deleteHookAndTags(hookId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                hookRepository.deleteHookAndTags(hookId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete hook and tags: $hookId", e)
                _errorMessage.value = "훅과 태그 삭제 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTagByTagName(tagName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                hookRepository.deleteTagByTagName(tagName)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete tag: $tagName", e)
                _errorMessage.value = "태그 삭제 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------------------- 데이터 업데이트---------------------- //

    fun updateHookAndTags(hook: Hook, selectedTags: List<String>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                hookRepository.updateHookAndTags(hook, selectedTags)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update hook and tags: ${hook.hookId}", e)
                _errorMessage.value = "훅과 태그 업데이트 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTagName(oldTagName: String, newTagName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                hookRepository.updateTagName(oldTagName, newTagName)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update tag name: $oldTagName -> $newTagName", e)
                _errorMessage.value = "태그 이름 변경 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setPinned(hookId: String, isPinned: Boolean) {
        viewModelScope.launch {
            try {
                hookRepository.setPinned(hookId, isPinned)
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update pin status: $hookId", e)
                _errorMessage.value = "고정 상태 변경 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    // ---------------------- 데이터 조회---------------------- //

    fun getTagsForHook(hookId: String): LiveData<List<Tag>> = hookRepository.getTagsForHook(hookId)

    /**
     * 특정 태그명으로 훅들을 조회
     * switchMap을 사용하여 메모리 누수를 방지
     */
    fun selectTagName(tagName: String) {
        if (_selectedTagName.value != tagName) {
            _selectedTagName.value = tagName
        }
    }

    /**
     * 선택된 태그를 초기화
     */
    fun clearSelectedTag() {
        _selectedTagName.value = null
    }

    /**
     * 에러 메시지를 초기화
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // ---------------------- Lifecycle 관리 ---------------------- //

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared")
    }
}
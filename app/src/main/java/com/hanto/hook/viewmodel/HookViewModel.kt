package com.hanto.hook.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.data.model.Event
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.HookWithTags
import com.hanto.hook.data.model.Tag
import com.hanto.hook.data.model.UiState
import com.hanto.hook.data.repository.HookRepository
import com.hanto.hook.util.JsoupUtils
import com.hanto.hook.util.SoundSearcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HookViewModel @Inject constructor(
    private val hookRepository: HookRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HookViewModel"
        const val TAG_UNCATEGORIZED = "미분류"
    }

    // ---------------------- StateFlow (UI State) ---------------------- //

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _deleteSuccessEvent = MutableSharedFlow<Unit>()
    val deleteSuccessEvent = _deleteSuccessEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 전체 훅 리스트
    val hookUiState: StateFlow<UiState> = combine(
        hookRepository.getAllHooks(),
        _searchQuery
    ) { hooks, query ->
        // 검색어가 비어있으면 전체 리스트 반환, 아니면 필터링
        if (query.isBlank()) {
            hooks
        } else {
            hooks.filter { item ->
                SoundSearcher.matchString(item.hook.title, query) ||
                        (item.hook.description?.let { SoundSearcher.matchString(it, query) }
                            ?: false)
            }
        }
    }
        .map { UiState.Success(it) as UiState }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    // 태그 이름 리스트
    val tagNames: StateFlow<List<String>> = hookRepository.getAllTagNames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 중복 제거된 태그 이름들
    val distinctTagNames: StateFlow<List<String>> = tagNames
        .map { it.distinct().sorted() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 선택된 태그 관리
    private val _selectedTagName = MutableStateFlow<String?>(null)
    val selectedTagName: StateFlow<String?> = _selectedTagName.asStateFlow()

    val homeTags: StateFlow<List<String>> = distinctTagNames
        .map { tags ->
            listOf(TAG_UNCATEGORIZED) + tags
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf(TAG_UNCATEGORIZED)
        )

    // 선택된 태그에 따른 훅 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    val hooksBySelectedTag: StateFlow<List<HookWithTags>> = _selectedTagName
        .flatMapLatest { tagName ->
            when {
                tagName.isNullOrBlank() -> flowOf(emptyList())
                tagName == TAG_UNCATEGORIZED -> hookRepository.getHooksWithNoTags()
                else -> hookRepository.getHooksByTagName(tagName)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 로딩 & 에러 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    // ---------------------- 데이터 조작 메서드 ---------------------- //

    fun insertHookWithTags(hook: Hook, tags: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 썸네일 이미지 크롤링
                val ogImageUrl = withContext(Dispatchers.IO) {
                    JsoupUtils.getOgImageUrl(hook.url)
                }

                val hookWithImage = hook.copy(imageUrl = ogImageUrl)

                // 훅 및 태그 삽입
                hookRepository.insertHook(hookWithImage)
                tags.forEach { tagName ->
                    val tag = Tag(hookId = hook.hookId, name = tagName)
                    hookRepository.insertTag(tag)
                }
                Log.d(TAG, "Hook with tags inserted: ${hook.title}")

                // 작업 완료 신호 전송
                _eventFlow.emit(Event.NavigateBack)

            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    Log.d(TAG, "Job cancelled", e)
                } else {
                    handleError("훅 저장 실패", e)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteHookAndTags(hookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                hookRepository.deleteHookAndTags(hookId)
                Log.d(TAG, "Hook and tags deleted: $hookId")
            } catch (e: Exception) {
                handleError("삭제 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateHookAndTags(hook: Hook, selectedTags: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val ogImageUrl = withContext(Dispatchers.IO) {
                    JsoupUtils.getOgImageUrl(hook.url)
                }

                // 이미지 갱신 처리
                val finalHook = if (!ogImageUrl.isNullOrBlank()) {
                    hook.copy(imageUrl = ogImageUrl)
                } else {
                    hook
                }

                hookRepository.updateHookAndTags(finalHook, selectedTags)
                Log.d(TAG, "Hook updated: ${hook.hookId}")

                _eventFlow.emit(Event.NavigateBack)

            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    Log.d(TAG, "Update job cancelled", e)
                } else {
                    handleError("업데이트 실패", e)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTagByTagName(tagName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                hookRepository.deleteTagByTagName(tagName)
                _deleteSuccessEvent.emit(Unit)
            } catch (e: Exception) {
                handleError("태그 삭제 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTagName(oldTagName: String, newTagName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                hookRepository.updateTagName(oldTagName, newTagName)
            } catch (e: Exception) {
                handleError("태그 수정 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setPinned(hookId: String, isPinned: Boolean) {
        viewModelScope.launch {
            try {
                hookRepository.setPinned(hookId, isPinned)
            } catch (e: Exception) {
                handleError("고정 상태 변경 실패", e)
            }
        }
    }

    fun getTagsForHook(hookId: String) = hookRepository.getTagsForHook(hookId)


    // ---------------------- 기타 메서드 ---------------------- //

    fun selectTagName(tagName: String) {
        _selectedTagName.value = tagName
    }

    fun clearSelectedTag() {
        _selectedTagName.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 에러 핸들링 공통 함수
    private fun handleError(msg: String, e: Exception) {
        Log.e(TAG, "$msg: ${e.message}", e)
        _errorMessage.value = "$msg: ${e.message}"
    }

}
package com.hanto.hook.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.usecase.AddHookUseCase
import com.hanto.hook.domain.usecase.DeleteHookUseCase
import com.hanto.hook.domain.usecase.DeleteTagUseCase
import com.hanto.hook.domain.usecase.ObserveHomeTagsUseCase
import com.hanto.hook.domain.usecase.ObserveHookTagsUseCase
import com.hanto.hook.domain.usecase.ObserveHooksByTagUseCase
import com.hanto.hook.domain.usecase.ObserveTagNamesUseCase
import com.hanto.hook.domain.usecase.RenameTagUseCase
import com.hanto.hook.domain.usecase.SearchHooksUseCase
import com.hanto.hook.domain.usecase.TogglePinUseCase
import com.hanto.hook.domain.usecase.UpdateHookUseCase
import com.hanto.hook.ui.model.Event
import com.hanto.hook.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 훅/태그 관련 화면들이 공유하는 ViewModel.
 *
 * 비즈니스 로직은 전부 UseCase로 위임하고, 이 클래스는 화면 상태 관리와
 * UseCase 호출·에러 전달만 담당한다.
 */
@HiltViewModel
class HookViewModel @Inject constructor(
    private val searchHooks: SearchHooksUseCase,
    private val observeTagNames: ObserveTagNamesUseCase,
    private val observeHomeTags: ObserveHomeTagsUseCase,
    private val observeHooksByTag: ObserveHooksByTagUseCase,
    private val observeHookTags: ObserveHookTagsUseCase,
    private val addHook: AddHookUseCase,
    private val updateHook: UpdateHookUseCase,
    private val deleteHook: DeleteHookUseCase,
    private val togglePin: TogglePinUseCase,
    private val renameTag: RenameTagUseCase,
    private val deleteTag: DeleteTagUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "HookViewModel"
        const val TAG_UNCATEGORIZED = com.hanto.hook.domain.model.TAG_UNCATEGORIZED
    }

    // ---------------------- 이벤트 / 상태 ---------------------- //

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _deleteSuccessEvent = MutableSharedFlow<Unit>()
    val deleteSuccessEvent = _deleteSuccessEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 전체 훅 리스트 (검색어 반영)
    val hookUiState: StateFlow<UiState> = searchHooks(_searchQuery)
        .map<List<Hook>, UiState> { UiState.Success(it) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    // 중복 제거·정렬된 태그 이름
    val distinctTagNames: StateFlow<List<String>> = observeTagNames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 홈/태그 화면용 (미분류 포함)
    val homeTags: StateFlow<List<String>> = observeHomeTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf(TAG_UNCATEGORIZED)
        )

    // 선택된 태그 관리
    private val _selectedTagName = MutableStateFlow<String?>(null)
    val selectedTagName: StateFlow<String?> = _selectedTagName.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val hooksBySelectedTag: StateFlow<List<Hook>> = _selectedTagName
        .flatMapLatest { tagName -> observeHooksByTag(tagName) }
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

    // ---------------------- 데이터 조작 ---------------------- //

    fun insertHookWithTags(hook: Hook, tags: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addHook(hook, tags)
                Log.d(TAG, "Hook with tags inserted: ${hook.title}")
                _eventFlow.emit(Event.NavigateBack)
            } catch (e: CancellationException) {
                Log.d(TAG, "Job cancelled", e)
                throw e
            } catch (e: Exception) {
                handleError("훅 저장 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateHookAndTags(hook: Hook, selectedTags: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                updateHook(hook, selectedTags)
                Log.d(TAG, "Hook updated: ${hook.hookId}")
                _eventFlow.emit(Event.NavigateBack)
            } catch (e: CancellationException) {
                Log.d(TAG, "Update job cancelled", e)
                throw e
            } catch (e: Exception) {
                handleError("업데이트 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteHookAndTags(hookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteHook(hookId)
                Log.d(TAG, "Hook and tags deleted: $hookId")
            } catch (e: Exception) {
                handleError("삭제 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTagByTagName(tagName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteTag(tagName)
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
                renameTag(oldTagName, newTagName)
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
                togglePin(hookId, isPinned)
            } catch (e: Exception) {
                handleError("고정 상태 변경 실패", e)
            }
        }
    }

    fun getTagsForHook(hookId: String) = observeHookTags(hookId)

    // ---------------------- 기타 ---------------------- //

    fun selectTagName(tagName: String) {
        _selectedTagName.value = tagName
    }

    fun clearSelectedTag() {
        _selectedTagName.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun handleError(msg: String, e: Exception) {
        Log.e(TAG, "$msg: ${e.message}", e)
        _errorMessage.value = "$msg: ${e.message}"
    }
}

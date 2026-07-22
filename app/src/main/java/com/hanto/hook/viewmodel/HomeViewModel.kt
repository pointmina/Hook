package com.hanto.hook.viewmodel

import androidx.lifecycle.viewModelScope
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.usecase.DeleteHookUseCase
import com.hanto.hook.domain.usecase.SearchHooksUseCase
import com.hanto.hook.domain.usecase.TogglePinUseCase
import com.hanto.hook.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchHooks: SearchHooksUseCase,
    private val togglePin: TogglePinUseCase,
    private val deleteHook: DeleteHookUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    private val _searchQuery = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 첫 구독 시 현재 값은 즉시 흘려보내고, 이후 타이핑만 디바운스한다.
    // (전체에 debounce를 걸면 최초 목록 표시까지 딜레이가 생긴다)
    private val debouncedSearchQuery = merge(
        _searchQuery.take(1),
        _searchQuery.drop(1).debounce(SEARCH_DEBOUNCE_MS)
    )

    val hookUiState: StateFlow<UiState> = searchHooks(debouncedSearchQuery)
        .map<List<Hook>, UiState> { UiState.Success(it) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun setPinned(hookId: String, isPinned: Boolean) {
        viewModelScope.launch {
            try {
                togglePin(hookId, isPinned)
            } catch (e: Exception) {
                handleError(TAG, "고정 상태 변경 실패", e)
            }
        }
    }

    fun deleteHookAndTags(hookId: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                deleteHook(hookId)
            } catch (e: Exception) {
                handleError(TAG, "삭제 실패", e)
            } finally {
                setLoading(false)
            }
        }
    }
}

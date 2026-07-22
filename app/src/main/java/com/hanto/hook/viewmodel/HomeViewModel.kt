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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    }

    private val _searchQuery = MutableStateFlow("")

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val hookUiState: StateFlow<UiState> = searchHooks(_searchQuery)
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

package com.hanto.hook.viewmodel

import androidx.lifecycle.viewModelScope
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.usecase.DeleteHookUseCase
import com.hanto.hook.domain.usecase.DeleteTagUseCase
import com.hanto.hook.domain.usecase.ObserveHooksByTagUseCase
import com.hanto.hook.domain.usecase.RenameTagUseCase
import com.hanto.hook.domain.usecase.TogglePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedTagViewModel @Inject constructor(
    private val observeHooksByTag: ObserveHooksByTagUseCase,
    private val renameTag: RenameTagUseCase,
    private val deleteTag: DeleteTagUseCase,
    private val togglePin: TogglePinUseCase,
    private val deleteHook: DeleteHookUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "SelectedTagViewModel"
    }

    private val _deleteSuccessEvent = MutableSharedFlow<Unit>()
    val deleteSuccessEvent = _deleteSuccessEvent.asSharedFlow()

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

    fun selectTagName(tagName: String) {
        _selectedTagName.value = tagName
    }

    fun clearSelectedTag() {
        _selectedTagName.value = null
    }

    fun updateTagName(oldTagName: String, newTagName: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                renameTag(oldTagName, newTagName)
            } catch (e: Exception) {
                handleError(TAG, "태그 수정 실패", e)
            } finally {
                setLoading(false)
            }
        }
    }

    fun deleteTagByTagName(tagName: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                deleteTag(tagName)
                _deleteSuccessEvent.emit(Unit)
            } catch (e: Exception) {
                handleError(TAG, "태그 삭제 실패", e)
            } finally {
                setLoading(false)
            }
        }
    }

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

package com.hanto.hook.viewmodel

import androidx.lifecycle.viewModelScope
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.usecase.ObserveHookTagsUseCase
import com.hanto.hook.domain.usecase.UpdateHookUseCase
import com.hanto.hook.ui.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HookDetailViewModel @Inject constructor(
    private val observeHookTags: ObserveHookTagsUseCase,
    private val updateHook: UpdateHookUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "HookDetailViewModel"
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getTagsForHook(hookId: String) = observeHookTags(hookId)

    fun updateHookAndTags(hook: Hook, selectedTags: List<String>) {
        viewModelScope.launch {
            setLoading(true)
            try {
                updateHook(hook, selectedTags)
                _eventFlow.emit(Event.NavigateBack)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleError(TAG, "업데이트 실패", e)
            } finally {
                setLoading(false)
            }
        }
    }
}

package com.hanto.hook.viewmodel

import androidx.lifecycle.viewModelScope
import com.hanto.hook.domain.model.Hook
import com.hanto.hook.domain.usecase.AddHookUseCase
import com.hanto.hook.ui.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHookViewModel @Inject constructor(
    private val addHook: AddHookUseCase,
) : BaseViewModel() {

    companion object {
        private const val TAG = "AddHookViewModel"
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun insertHookWithTags(hook: Hook, tags: List<String>) {
        viewModelScope.launch {
            setLoading(true)
            try {
                addHook(hook, tags)
                _eventFlow.emit(Event.NavigateBack)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                handleError(TAG, "훅 저장 실패", e)
            } finally {
                setLoading(false)
            }
        }
    }
}

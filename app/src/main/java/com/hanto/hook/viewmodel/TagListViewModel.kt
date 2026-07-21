package com.hanto.hook.viewmodel

import androidx.lifecycle.viewModelScope
import com.hanto.hook.domain.usecase.ObserveTagNamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TagListViewModel @Inject constructor(
    private val observeTagNames: ObserveTagNamesUseCase,
) : BaseViewModel() {

    val distinctTagNames: StateFlow<List<String>> = observeTagNames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

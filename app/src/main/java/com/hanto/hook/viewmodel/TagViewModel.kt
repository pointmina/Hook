package com.hanto.hook.viewmodel

import androidx.lifecycle.viewModelScope
import com.hanto.hook.domain.model.TAG_UNCATEGORIZED
import com.hanto.hook.domain.usecase.ObserveHomeTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val observeHomeTags: ObserveHomeTagsUseCase,
) : BaseViewModel() {

    val homeTags: StateFlow<List<String>> = observeHomeTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf(TAG_UNCATEGORIZED)
        )
}

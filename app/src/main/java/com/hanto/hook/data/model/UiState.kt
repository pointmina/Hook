package com.hanto.hook.data.model

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<HookWithTags>) : UiState()
    data class Error(val message: String) : UiState()
}
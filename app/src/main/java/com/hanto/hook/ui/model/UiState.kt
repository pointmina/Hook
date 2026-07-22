package com.hanto.hook.ui.model

import com.hanto.hook.domain.model.Hook

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<Hook>) : UiState()
    data class Error(val message: String) : UiState()
}

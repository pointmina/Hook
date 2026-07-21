package com.hanto.hook.ui.model

sealed class Event {
    object NavigateBack : Event()
    data class ShowToast(val message: String) : Event()
}

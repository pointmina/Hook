package com.hanto.hook.data.model

sealed class Event {
    object NavigateBack : Event()
    data class ShowToast(val message: String) : Event()
}


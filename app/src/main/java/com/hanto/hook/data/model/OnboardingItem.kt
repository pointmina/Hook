package com.hanto.hook.data.model

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageResId: Int,
    val isLastPage: Boolean = false
)

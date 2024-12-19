package com.hanto.hook.data

import com.hanto.hook.data.model.Hook


data class HookWithTags(
    val hook: Hook,
    val tags: List<String>
)

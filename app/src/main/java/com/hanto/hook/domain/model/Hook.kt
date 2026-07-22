package com.hanto.hook.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 앱 전역에서 사용하는 순수 도메인 모델.
 *
 * Room/네트워크 등 프레임워크 세부사항을 알지 못한다.
 * 화면 간 Intent 전달을 위해 Parcelable만 구현한다.
 */
@Parcelize
data class Hook(
    val hookId: String,
    val title: String,
    val url: String?,
    val description: String?,
    val isPinned: Boolean = false,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
) : Parcelable

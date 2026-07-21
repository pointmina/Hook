package com.hanto.hook.data.mapper

import com.hanto.hook.data.local.entity.HookEntity
import com.hanto.hook.data.local.entity.TagEntity
import com.hanto.hook.data.local.relation.HookWithTagsEntity
import com.hanto.hook.domain.model.Hook

/**
 * 데이터 레이어와 도메인 레이어의 경계에서만 변환을 수행한다.
 */

fun HookWithTagsEntity.toDomain(): Hook = Hook(
    hookId = hook.hookId,
    title = hook.title,
    url = hook.url,
    description = hook.description,
    isPinned = hook.isPinned,
    imageUrl = hook.imageUrl,
    tags = tags.map { it.name }
)

fun List<HookWithTagsEntity>.toDomain(): List<Hook> = map { it.toDomain() }

/** 도메인 훅을 저장용 엔티티로 변환한다. surrogate id는 Room이 자동 생성한다. */
fun Hook.toEntity(): HookEntity = HookEntity(
    hookId = hookId,
    title = title,
    url = url,
    description = description,
    isPinned = isPinned,
    imageUrl = imageUrl
)

/** 도메인 훅의 태그 이름들을 태그 엔티티 목록으로 변환한다. */
fun Hook.toTagEntities(): List<TagEntity> =
    tags.map { name -> TagEntity(hookId = hookId, name = name) }

package com.hanto.hook.di

import com.hanto.hook.data.repository.HookRepositoryImpl
import com.hanto.hook.data.repository.MetadataRepositoryImpl
import com.hanto.hook.domain.repository.HookRepository
import com.hanto.hook.domain.repository.MetadataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 도메인 인터페이스를 데이터 레이어 구현체에 바인딩한다(의존성 역전 배선).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHookRepository(impl: HookRepositoryImpl): HookRepository

    @Binds
    @Singleton
    abstract fun bindMetadataRepository(impl: MetadataRepositoryImpl): MetadataRepository
}

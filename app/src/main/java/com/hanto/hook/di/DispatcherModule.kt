package com.hanto.hook.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * IO 작업용 디스패처를 식별하는 한정자.
 * 하드코딩된 Dispatchers.IO 대신 주입받아 테스트 시 교체 가능하게 한다.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * 화면이 사라져도(ViewModel clear) 계속 진행되어야 하는 백그라운드 작업용 스코프.
 * viewModelScope에 묶으면 저장 직후 화면을 벗어날 때 함께 취소되므로,
 * 썸네일 크롤링처럼 "저장은 끝났지만 후처리가 남은" 작업에 사용한다.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
}

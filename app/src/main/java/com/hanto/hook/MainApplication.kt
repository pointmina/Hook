package com.hanto.hook

import android.app.Application
import com.hanto.hook.di.ApplicationScope
import com.hanto.hook.domain.usecase.SeedSampleDataUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var seedSampleData: SeedSampleDataUseCase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            applicationScope.launch {
                seedSampleData()
            }
        }
    }
}

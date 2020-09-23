package de.schnettler.repo.di

import android.app.Application
import androidx.work.WorkManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.schnettler.repo.util.defaultSharedPrefs
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideFlowSharedPrefs(application: Application): FlowSharedPreferences = FlowSharedPreferences(
        application.defaultSharedPrefs()
    )

    @Provides
    fun provideWorkManager(application: Application) = WorkManager.getInstance(application)
}
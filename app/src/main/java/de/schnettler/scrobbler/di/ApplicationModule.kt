package de.schnettler.scrobbler.di

import android.app.Application
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideWorkManager(application: Application) = WorkManager.getInstance(application)
}
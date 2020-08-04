package de.schnettler.repo.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Suppress("TooManyFunctions")
@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideServiceScope() = serviceCoroutineScope(Job() + Dispatchers.IO)

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences("sessionPreferences", Context.MODE_PRIVATE)

    @Provides
    fun provideWorkManager(application: Application) = WorkManager.getInstance(application)
}

interface ServiceCoroutineScope : CoroutineScope

fun serviceCoroutineScope(
    context: CoroutineContext
): ServiceCoroutineScope = object : ServiceCoroutineScope {
    override val coroutineContext = context + Dispatchers.IO
}
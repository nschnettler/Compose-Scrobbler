package de.schnettler.repo.di

import android.app.Application
import androidx.work.WorkManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.schnettler.repo.util.defaultSharedPrefs
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
    @Singleton
    fun provideFlowSharedPrefs(application: Application): FlowSharedPreferences = FlowSharedPreferences(
        application.defaultSharedPrefs()
    )

    @Provides
    fun provideWorkManager(application: Application) = WorkManager.getInstance(application)
}

interface ServiceCoroutineScope : CoroutineScope

fun serviceCoroutineScope(
    context: CoroutineContext
): ServiceCoroutineScope = object : ServiceCoroutineScope {
    override val coroutineContext = context + Dispatchers.IO
}
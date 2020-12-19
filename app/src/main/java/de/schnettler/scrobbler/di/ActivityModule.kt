package de.schnettler.scrobbler.di

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
    @Provides
    fun provideActivityCoroutineScope(activity: Activity): CoroutineScope =
        (activity as AppCompatActivity).lifecycleScope

    @Provides
    fun provideCoroutineContext(coroutineScope: CoroutineScope) = coroutineScope.coroutineContext
}
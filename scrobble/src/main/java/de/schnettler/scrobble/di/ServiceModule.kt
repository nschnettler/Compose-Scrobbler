package de.schnettler.scrobble.di

import android.app.NotificationManager
import android.app.Service
import android.content.Context.NOTIFICATION_SERVICE
import androidx.lifecycle.lifecycleScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import de.schnettler.scrobble.MediaListenerService
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {
    @Provides
    fun provideNotificationManager(service: Service) =
        service.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun coroutineScope(service: Service): CoroutineScope = (service as MediaListenerService).lifecycleScope
}
package de.schnettler.repo

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.ServiceComponent
import de.schnettler.database.AppDatabase
import de.schnettler.lastfm.api.*
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.spotify.SpotifyAuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideLastFmService() = provideRetrofit(
        provideOkHttpClient(LastFMInterceptor(), loggingInterceptor), LastFmService.ENDPOINT
    ).create(
        LastFmService::class.java
    )

    @Provides
    @Singleton
    fun spotifyAuthService() = provideRetrofit(
        provideOkHttpClient(SpotifyAuthInterceptor(), loggingInterceptor),
        SpotifyAuthService.AUTH_ENDPOINT
    ).create(
        SpotifyAuthService::class.java
    )
}

//@Module
//@InstallIn(ServiceComponent::class)
//class ServiceModule {
//
//}

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application) =
        de.schnettler.database.provideDatabase(application)

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideArtistDao(database: AppDatabase) = database.artistDao()

    @Provides
    @Singleton
    fun provideAlbumDao(database: AppDatabase) = database.albumDao()

    @Provides
    @Singleton
    fun provideTrackDao(database: AppDatabase) = database.trackDao()

    @Provides
    @Singleton
    fun provideChartDao(database: AppDatabase) = database.chartDao()

    @Provides
    @Singleton
    fun provideAuthDao(database: AppDatabase) = database.authDao()

    @Provides
    @Singleton
    fun provideRelationDao(database: AppDatabase) = database.relationshipDao()

    @Provides
    @Singleton
    fun provideLocalTrackDao(database: AppDatabase) = database.localTrackDao()

    @Provides
    @Singleton
    fun provideServiceScope() = ServiceCoroutineScope(Job() + Dispatchers.IO)
}

interface ServiceCoroutineScope : CoroutineScope

fun ServiceCoroutineScope(
        context: CoroutineContext
): ServiceCoroutineScope = object : ServiceCoroutineScope {
    override val coroutineContext = context + Dispatchers.IO
}


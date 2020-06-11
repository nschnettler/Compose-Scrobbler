package de.schnettler.repo

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.schnettler.database.AppDatabase
import de.schnettler.lastfm.api.*
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.spotify.SpotifyAuthService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideLastFmService() = provideRetrofit(
        provideOkHttpClient(LastFMInterceptor(), loggingInterceptor), LastFmService.ENDPOINT
    ).create(
        LastFmService::class.java)

    @Provides
    @Singleton
    fun spotifyAuthService() = provideRetrofit(
            provideOkHttpClient(SpotifyAuthInterceptor(), loggingInterceptor), SpotifyAuthService.AUTH_ENDPOINT
        ).create(
            SpotifyAuthService::class.java
        )
}

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application) = de.schnettler.database.provideDatabase(application)

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
}
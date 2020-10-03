package de.schnettler.repo.di

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.schnettler.lastfm.api.LastFMInterceptor
import de.schnettler.lastfm.api.SpotifyAuthInterceptor
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.lastfm.PostService
import de.schnettler.lastfm.api.loggingInterceptor
import de.schnettler.lastfm.api.provideOkHttpClient
import de.schnettler.lastfm.api.provideRetrofit
import de.schnettler.lastfm.api.spotify.SpotifyAuthService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideLastFmService(application: Application): LastFmService = provideRetrofit(
        provideOkHttpClient(LastFMInterceptor(), loggingInterceptor, ChuckerInterceptor(application)), LastFmService.ENDPOINT
    ).create(
        LastFmService::class.java
    )

    @Provides
    @Singleton
    fun provideScrobblerService(): PostService = provideRetrofit(
        provideOkHttpClient(loggingInterceptor), LastFmService.ENDPOINT
    ).create(
        PostService::class.java
    )

    @Provides
    @Singleton
    fun spotifyAuthService(): SpotifyAuthService = provideRetrofit(
        provideOkHttpClient(SpotifyAuthInterceptor(), loggingInterceptor),
        SpotifyAuthService.AUTH_ENDPOINT
    ).create(
        SpotifyAuthService::class.java
    )
}
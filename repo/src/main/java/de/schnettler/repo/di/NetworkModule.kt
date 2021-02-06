package de.schnettler.repo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.api.SpotifyAuthInterceptor
import de.schnettler.lastfm.api.loggingInterceptor
import de.schnettler.lastfm.api.provideOkHttpClient
import de.schnettler.lastfm.api.provideRetrofit
import de.schnettler.lastfm.api.spotify.SpotifyAuthService

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun spotifyAuthService(): SpotifyAuthService = provideRetrofit(
        provideOkHttpClient(SpotifyAuthInterceptor(), loggingInterceptor),
        SpotifyAuthService.AUTH_ENDPOINT
    ).create(
        SpotifyAuthService::class.java
    )
}
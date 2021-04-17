package de.schnettler.scrobbler.network.spotify.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.scrobbler.network.spotify.annotation.retrofit.AuthorizedSpotifyRetrofitClient
import de.schnettler.scrobbler.network.spotify.annotation.retrofit.BasicSpotifyRetrofitClient
import de.schnettler.scrobbler.network.spotify.api.SpotifyLoginService
import de.schnettler.scrobbler.network.spotify.api.SpotifySearchService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    @Provides
    fun providesLoginService(
        @BasicSpotifyRetrofitClient retrofit: Retrofit
    ): SpotifyLoginService = retrofit.create(SpotifyLoginService::class.java)

    @Provides
    fun providesSearchService(
        @AuthorizedSpotifyRetrofitClient retrofit: Retrofit
    ): SpotifySearchService = retrofit.create(SpotifySearchService::class.java)
}
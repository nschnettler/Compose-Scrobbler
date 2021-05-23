package de.schnettler.scrobbler.image.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.scrobbler.image.api.SpotifyApi
import de.schnettler.scrobbler.network.spotify.annotation.retrofit.AuthorizedSpotifyRetrofitClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class SpotifyApiModule {

    @Provides
    fun providesSearchService(
        @AuthorizedSpotifyRetrofitClient retrofit: Retrofit
    ): SpotifyApi = retrofit.create(SpotifyApi::class.java)
}
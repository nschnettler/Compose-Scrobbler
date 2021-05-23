package de.schnettler.scrobbler.authentication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.SignedLastfmRetrofitClient
import de.schnettler.scrobbler.authentication.api.LastfmSessionApi
import de.schnettler.scrobbler.authentication.api.SpotifyAuthApi
import de.schnettler.scrobbler.network.spotify.annotation.retrofit.BasicSpotifyRetrofitClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class AuthApiModule {
    @Provides
    fun providesLoginService(
        @BasicSpotifyRetrofitClient retrofit: Retrofit
    ): SpotifyAuthApi = retrofit.create(SpotifyAuthApi::class.java)

    @Provides
    fun providesSessionService(
        @SignedLastfmRetrofitClient retrofit: Retrofit
    ): LastfmSessionApi = retrofit.create(LastfmSessionApi::class.java)
}
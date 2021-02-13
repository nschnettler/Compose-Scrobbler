package de.schnettler.scrobbler.network.spotify.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.scrobbler.network.spotify.annotation.okhttp.AuthorizedSpotifyHttpClient
import de.schnettler.scrobbler.network.spotify.annotation.okhttp.BasicSpotifyHttpClient
import de.schnettler.scrobbler.network.spotify.annotation.retrofit.AuthorizedSpotifyRetrofitClient
import de.schnettler.scrobbler.network.spotify.annotation.retrofit.BasicSpotifyRetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RetrofitClientModule {
    @AuthorizedSpotifyRetrofitClient
    @Provides
    fun provideAuthenticatedRetrofit(
        @AuthorizedSpotifyHttpClient okHttpClient: OkHttpClient,
        @BasicSpotifyRetrofitClient retrofit: Retrofit,
    ): Retrofit = retrofit.newBuilder()
        .baseUrl("https://api.spotify.com/v1/")
        .client(okHttpClient)
        .build()

    @BasicSpotifyRetrofitClient
    @Provides
    fun provideBasicRetrofit(
        @BasicSpotifyHttpClient okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://accounts.spotify.com/api/")
        .addConverterFactory(converterFactory)
        .build()
}
package de.schnettler.scrobbler.network.spotify.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.scrobbler.network.common.annotation.okhttp.BaseOkHttpClient
import de.schnettler.scrobbler.network.spotify.AccessTokenAuthenticator
import de.schnettler.scrobbler.network.spotify.annotation.okhttp.AuthorizedSpotifyHttpClient
import de.schnettler.scrobbler.network.spotify.annotation.okhttp.BasicSpotifyHttpClient
import de.schnettler.scrobbler.network.spotify.interceptor.BasicHeaderInterceptor
import de.schnettler.scrobbler.network.spotify.interceptor.BearerHeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
class OkHttpClientModule {

    @AuthorizedSpotifyHttpClient
    @Provides
    fun providesAuthorizedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        bearerHeaderInterceptor: BearerHeaderInterceptor,
        spotifyAuthenticator: AccessTokenAuthenticator,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(bearerHeaderInterceptor)
        .authenticator(spotifyAuthenticator)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    @BasicSpotifyHttpClient
    @Provides
    fun providesBasicOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        basicHeaderInterceptor: BasicHeaderInterceptor,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(basicHeaderInterceptor)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()
}

package de.schnettler.lastfm.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.okhttp.AuthorizedLastfmHttpClient
import de.schnettler.lastfm.annotation.okhttp.BasicLastfmHttpClient
import de.schnettler.lastfm.annotation.okhttp.SignedLastfmHttpClient
import de.schnettler.lastfm.interceptor.LastfmInterceptor
import de.schnettler.scrobbler.network.common.annotation.okhttp.BaseOkHttpClient
import de.schnettler.lastfm.interceptor.SessionInterceptor
import de.schnettler.lastfm.interceptor.SignatureInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
class OkHttpClientModule {

    @AuthorizedLastfmHttpClient
    @Provides
    fun providesAuthorizedOkHttpClient(
        sessionInterceptor: SessionInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        lastfmInterceptor: LastfmInterceptor,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(lastfmInterceptor)
        .addInterceptor(sessionInterceptor)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    @SignedLastfmHttpClient
    @Provides
    fun providesSignatureOkHttpClient(
        signatureInterceptor: SignatureInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        sessionInterceptor: SessionInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        lastfmInterceptor: LastfmInterceptor,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(lastfmInterceptor)
        .addInterceptor(sessionInterceptor)
        .addInterceptor(signatureInterceptor)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    @BasicLastfmHttpClient
    @Provides
    fun providesBasicOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        lastfmInterceptor: LastfmInterceptor,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(lastfmInterceptor)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()
}

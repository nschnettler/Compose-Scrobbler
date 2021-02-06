package de.schnettler.lastfm.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.di.okhttp.AuthorizedOkHttpClient
import de.schnettler.lastfm.di.okhttp.BaseOkHttpClient
import de.schnettler.lastfm.di.okhttp.BasicOkHttpClient
import de.schnettler.lastfm.di.okhttp.SignatureOkHttpClient
import de.schnettler.lastfm.interceptor.LastfmInterceptor
import de.schnettler.lastfm.interceptor.SessionInterceptor
import de.schnettler.lastfm.interceptor.SignatureInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
class OkHttpClientModule {

    @AuthorizedOkHttpClient
    @Provides
    fun providesAuthorizedOkHttpClient(
        sessionInterceptor: SessionInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .addInterceptor(sessionInterceptor)
        .build()

    @SignatureOkHttpClient
    @Provides
    fun providesSignatureOkHttpClient(
        signatureInterceptor: SignatureInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        sessionInterceptor: SessionInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(sessionInterceptor)
        .addInterceptor(signatureInterceptor)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    @BasicOkHttpClient
    @Provides
    fun providesBasicOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        @BaseOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    @BaseOkHttpClient
    @Provides
    fun providesBaseOkHttpClient(
        lastfmInterceptor: LastfmInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(lastfmInterceptor)
        .build()
}

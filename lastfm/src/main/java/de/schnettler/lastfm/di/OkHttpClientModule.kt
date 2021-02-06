package de.schnettler.lastfm.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.di.okhttp.AuthorizedOkHttpClient
import de.schnettler.lastfm.di.okhttp.BasicOkHttpClient
import de.schnettler.lastfm.interceptor.LastfmInterceptor
import de.schnettler.lastfm.interceptor.SessionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
class OkHttpClientModule {

    @AuthorizedOkHttpClient
    @Provides
    fun providesAuthorizedOkHttpClient(
        sessionInterceptor: SessionInterceptor,
        @BasicOkHttpClient okHttpClient: OkHttpClient,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(sessionInterceptor)
        .build()

    @BasicOkHttpClient
    @Provides
    fun providesBasicOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        lastfmInterceptor: LastfmInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .addInterceptor(lastfmInterceptor)
        .build()
}

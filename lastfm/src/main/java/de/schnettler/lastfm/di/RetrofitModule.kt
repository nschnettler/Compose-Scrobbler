package de.schnettler.lastfm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.di.okhttp.AuthorizedOkHttpClient
import de.schnettler.lastfm.di.okhttp.BasicOkHttpClient
import de.schnettler.lastfm.di.okhttp.SignatureOkHttpClient
import de.schnettler.lastfm.di.retrofit.AuthorizedRetrofitClient
import de.schnettler.lastfm.di.retrofit.BasicRetrofitClient
import de.schnettler.lastfm.di.retrofit.SignedRetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @SignedRetrofitClient
    @Provides
    fun provideSignatureRetrofit(
        @SignatureOkHttpClient okHttpClient: OkHttpClient,
        @BasicRetrofitClient retrofit: Retrofit,
    ): Retrofit = retrofit.newBuilder()
        .client(okHttpClient)
        .build()

    @AuthorizedRetrofitClient
    @Provides
    fun provideAuthenticatedRetrofit(
        @AuthorizedOkHttpClient okHttpClient: OkHttpClient,
        @BasicRetrofitClient retrofit: Retrofit,
    ): Retrofit = retrofit.newBuilder()
        .client(okHttpClient)
        .build()

    @BasicRetrofitClient
    @Provides
    fun provideBasicRetrofit(
        @BasicOkHttpClient okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(LastFmService.ENDPOINT)
        .addConverterFactory(converterFactory)
        .build()
}
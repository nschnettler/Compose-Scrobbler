package de.schnettler.lastfm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.di.okhttp.AuthorizedOkHttpClient
import de.schnettler.lastfm.di.okhttp.BasicOkHttpClient
import de.schnettler.lastfm.di.retrofit.AuthorizedRetrofitClient
import de.schnettler.lastfm.di.retrofit.BasicRetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @AuthorizedRetrofitClient
    @Provides
    fun provideAuthenticatedRetrofit(
        @AuthorizedOkHttpClient okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(LastFmService.ENDPOINT)
        .addConverterFactory(converterFactory)
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
package de.schnettler.lastfm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.okhttp.AuthorizedLastfmHttpClient
import de.schnettler.lastfm.annotation.okhttp.BasicLastfmHttpClient
import de.schnettler.lastfm.annotation.okhttp.SignedLastfmHttpClient
import de.schnettler.lastfm.annotation.retrofit.AuthorizedLastfmRetrofitClient
import de.schnettler.lastfm.annotation.retrofit.BasicLastfmRetrofitClient
import de.schnettler.lastfm.annotation.retrofit.SignedLastfmRetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RetrofitClientModule {
    @SignedLastfmRetrofitClient
    @Provides
    fun provideSignatureRetrofit(
        @SignedLastfmHttpClient okHttpClient: OkHttpClient,
        @BasicLastfmRetrofitClient retrofit: Retrofit,
    ): Retrofit = retrofit.newBuilder()
        .client(okHttpClient)
        .build()

    @AuthorizedLastfmRetrofitClient
    @Provides
    fun provideAuthenticatedRetrofit(
        @AuthorizedLastfmHttpClient okHttpClient: OkHttpClient,
        @BasicLastfmRetrofitClient retrofit: Retrofit,
    ): Retrofit = retrofit.newBuilder()
        .client(okHttpClient)
        .build()

    @BasicLastfmRetrofitClient
    @Provides
    fun provideBasicRetrofit(
        @BasicLastfmHttpClient okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://ws.audioscrobbler.com/2.0/")
        .addConverterFactory(converterFactory)
        .build()
}
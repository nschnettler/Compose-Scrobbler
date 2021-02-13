package de.schnettler.scrobbler.network.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.scrobbler.network.common.annotation.okhttp.BaseOkHttpClient
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class BaseOkHttpClientModule {
    @BaseOkHttpClient
    @Provides
    fun providesBaseOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .build()
}
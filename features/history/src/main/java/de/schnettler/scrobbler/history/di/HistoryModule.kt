package de.schnettler.scrobbler.history.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.AuthorizedLastfmRetrofitClient
import de.schnettler.scrobbler.history.api.HistoryApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class HistoryModule {
    @Provides
    fun providesHistoryApi(
        @AuthorizedLastfmRetrofitClient retrofit: Retrofit
    ): HistoryApi = retrofit.create(HistoryApi::class.java)
}
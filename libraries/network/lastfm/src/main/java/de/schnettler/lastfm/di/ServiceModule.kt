package de.schnettler.lastfm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.SignedLastfmRetrofitClient
import de.schnettler.lastfm.api.lastfm.SessionService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    @Provides
    fun providesSessionService(
        @SignedLastfmRetrofitClient retrofit: Retrofit
    ): SessionService = retrofit.create(SessionService::class.java)
}
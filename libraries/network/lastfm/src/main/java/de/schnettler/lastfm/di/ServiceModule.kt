package de.schnettler.lastfm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.AuthorizedLastfmRetrofitClient
import de.schnettler.lastfm.annotation.retrofit.BasicLastfmRetrofitClient
import de.schnettler.lastfm.annotation.retrofit.SignedLastfmRetrofitClient
import de.schnettler.lastfm.api.lastfm.ChartService
import de.schnettler.lastfm.api.lastfm.SessionService
import de.schnettler.lastfm.api.lastfm.UserService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    @Provides
    fun providesUserService(
        @AuthorizedLastfmRetrofitClient retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    fun providesChartService(
        @BasicLastfmRetrofitClient retrofit: Retrofit
    ): ChartService = retrofit.create(ChartService::class.java)

    @Provides
    fun providesSessionService(
        @SignedLastfmRetrofitClient retrofit: Retrofit
    ): SessionService = retrofit.create(SessionService::class.java)
}
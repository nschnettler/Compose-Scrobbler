package de.schnettler.scrobbler.charts.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.BasicLastfmRetrofitClient
import de.schnettler.scrobbler.charts.api.ChartApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ChartApiModule {
    @Provides
    fun providesChartService(
        @BasicLastfmRetrofitClient retrofit: Retrofit
    ): ChartApi = retrofit.create(ChartApi::class.java)

}
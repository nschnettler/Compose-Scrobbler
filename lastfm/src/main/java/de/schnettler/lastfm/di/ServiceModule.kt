package de.schnettler.lastfm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.api.lastfm.ArtistService
import de.schnettler.lastfm.api.lastfm.ChartService
import de.schnettler.lastfm.api.lastfm.DetailService
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.lastfm.SearchService
import de.schnettler.lastfm.api.lastfm.SessionService
import de.schnettler.lastfm.api.lastfm.UserService
import de.schnettler.lastfm.di.retrofit.AuthorizedRetrofitClient
import de.schnettler.lastfm.di.retrofit.BasicRetrofitClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    @Provides
    fun providesLastFmService(
        @AuthorizedRetrofitClient retrofit: Retrofit
    ): LastFmService = retrofit.create(LastFmService::class.java)

    @Provides
    fun providesUserService(
        @AuthorizedRetrofitClient retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    fun providesDetailService(
        @AuthorizedRetrofitClient retrofit: Retrofit
    ): DetailService = retrofit.create(DetailService::class.java)

    @Provides
    fun providesBasicService(
        @BasicRetrofitClient retrofit: Retrofit
    ): ArtistService = retrofit.create(ArtistService::class.java)

    @Provides
    fun providesSearchService(
        @BasicRetrofitClient retrofit: Retrofit
    ): SearchService = retrofit.create(SearchService::class.java)

    @Provides
    fun providesChartService(
        @BasicRetrofitClient retrofit: Retrofit
    ): ChartService = retrofit.create(ChartService::class.java)

    @Provides
    fun providesSessionService(
        @BasicRetrofitClient retrofit: Retrofit
    ): SessionService = retrofit.create(SessionService::class.java)
}
package de.schnettler.scrobbler.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.BasicLastfmRetrofitClient
import de.schnettler.scrobbler.search.api.SearchService
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class SearchModule {
    @Provides
    fun providesSearchService(
        @BasicLastfmRetrofitClient retrofit: Retrofit
    ): SearchService = retrofit.create(SearchService::class.java)
}
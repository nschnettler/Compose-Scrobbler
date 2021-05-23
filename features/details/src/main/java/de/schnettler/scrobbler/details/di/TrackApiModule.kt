package de.schnettler.scrobbler.details.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.AuthorizedLastfmRetrofitClient
import de.schnettler.lastfm.annotation.retrofit.SignedLastfmRetrofitClient
import de.schnettler.scrobbler.details.api.TrackApi
import de.schnettler.scrobbler.details.api.TrackPostApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class TrackApiModule {
    @Provides
    fun provideTrackApi(
        @AuthorizedLastfmRetrofitClient retrofit: Retrofit,
    ): TrackApi = retrofit.create(TrackApi::class.java)

    @Provides
    fun provideTrackPostApi(
        @SignedLastfmRetrofitClient retrofit: Retrofit,
    ): TrackPostApi = retrofit.create(TrackPostApi::class.java)
}
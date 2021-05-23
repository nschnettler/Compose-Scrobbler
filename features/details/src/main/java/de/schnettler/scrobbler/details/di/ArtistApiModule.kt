package de.schnettler.scrobbler.details.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.AuthorizedLastfmRetrofitClient
import de.schnettler.scrobbler.details.api.ArtistApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ArtistApiModule {
    @Provides
    fun provideArtistApi(
        @AuthorizedLastfmRetrofitClient retrofit: Retrofit,
    ): ArtistApi = retrofit.create(ArtistApi::class.java)
}

package de.schnettler.scrobbler.details.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.AuthorizedLastfmRetrofitClient
import de.schnettler.scrobbler.details.api.AlbumApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class AlbumApiModule {
    @Provides
    fun provideAlbumApi(
        @AuthorizedLastfmRetrofitClient retrofit: Retrofit,
    ): AlbumApi = retrofit.create(AlbumApi::class.java)
}

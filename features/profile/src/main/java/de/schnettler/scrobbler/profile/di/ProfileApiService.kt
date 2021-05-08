package de.schnettler.scrobbler.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.AuthorizedLastfmRetrofitClient
import de.schnettler.scrobbler.profile.api.ProfileApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ProfileApiService {
    @Provides
    fun providesUserService(
        @AuthorizedLastfmRetrofitClient retrofit: Retrofit
    ): ProfileApi = retrofit.create(ProfileApi::class.java)
}
package de.schnettler.scrobbler.submission.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.annotation.retrofit.SignedLastfmRetrofitClient
import de.schnettler.scrobbler.submission.api.SubmissionApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class SubmissionModule {
    @Provides
    fun providesSubmissionApi(
        @SignedLastfmRetrofitClient retrofit: Retrofit
    ): SubmissionApi = retrofit.create(SubmissionApi::class.java)
}
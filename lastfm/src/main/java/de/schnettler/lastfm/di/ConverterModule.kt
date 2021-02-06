package de.schnettler.lastfm.di

import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class ConverterModule {
    @Provides
    fun provideMoshi(): MoshiConverterFactory = MoshiConverterFactory.create(
        Moshi.Builder()
            .add(Wrapped.ADAPTER_FACTORY)
            .build()
    )
}
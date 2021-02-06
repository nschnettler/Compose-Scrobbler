package de.schnettler.repo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.AuthProvider
import de.schnettler.repo.authentication.provider.LastFmAuthProvider

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    fun providesLastFmAuthProvider(lastFmAuthProvider: LastFmAuthProvider): AuthProvider
}
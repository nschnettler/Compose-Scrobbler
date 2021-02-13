package de.schnettler.repo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.LastFmAuthProvider
import de.schnettler.repo.authentication.provider.LastFmAuthProviderImpl
import de.schnettler.repo.authentication.provider.SpotifyAuthProviderImpl
import de.schnettler.scrobbler.network.spotify.SpotifyAuthProvider

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    fun providesLastFmAuthProvider(lastFmAuthProvider: LastFmAuthProviderImpl): LastFmAuthProvider

    @Binds
    fun providesSpotifyAuthProvider(spotifyAuthProvider: SpotifyAuthProviderImpl): SpotifyAuthProvider
}
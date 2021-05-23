package de.schnettler.scrobbler.authentication.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.lastfm.LastFmAuthProvider
import de.schnettler.scrobbler.network.spotify.SpotifyAuthProvider
import de.schnettler.scrobbler.authentication.provider.LastFmAuthProviderImpl
import de.schnettler.scrobbler.authentication.provider.SpotifyAuthProviderImpl

@Module
@InstallIn(SingletonComponent::class)
interface AuthProviderModule {
    @Binds
    fun providesLastFmAuthProvider(lastFmAuthProvider: LastFmAuthProviderImpl): LastFmAuthProvider

    @Binds
    fun providesSpotifyAuthProvider(spotifyAuthProvider: SpotifyAuthProviderImpl): SpotifyAuthProvider
}
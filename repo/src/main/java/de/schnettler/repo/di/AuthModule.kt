package de.schnettler.repo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.repo.authentication.AccessTokenAuthenticator
import de.schnettler.repo.authentication.provider.LastFmAuthProviderImpl
import de.schnettler.repo.authentication.provider.SpotifyAuthProviderImpl
import de.schnettler.lastfm.LastFmAuthProvider
import de.schnettler.scrobbler.network.spotify.SpotifyAuthProvider
import okhttp3.Authenticator

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    fun providesLastFmAuthProvider(lastFmAuthProvider: LastFmAuthProviderImpl): LastFmAuthProvider

    @Binds
    fun providesSpotifyAuthProvider(spotifyAuthProvider: SpotifyAuthProviderImpl): SpotifyAuthProvider

    @Binds
    fun providesSpotifyAuthenticator(spotifyAuthenticator: AccessTokenAuthenticator): Authenticator
}
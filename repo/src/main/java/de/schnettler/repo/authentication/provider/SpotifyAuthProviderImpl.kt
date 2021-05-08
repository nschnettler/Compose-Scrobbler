package de.schnettler.repo.authentication.provider

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.AuthTokenType
import de.schnettler.repo.mapping.auth.SpotifyAuthMapper
import de.schnettler.scrobbler.network.spotify.SpotifyAuthProvider
import de.schnettler.scrobbler.network.spotify.api.SpotifyLoginService
import javax.inject.Inject

class SpotifyAuthProviderImpl @Inject constructor(
    private val service: SpotifyLoginService,
    private val dao: AuthDao
) : SpotifyAuthProvider {
    private suspend fun getToken() = spotifyTokenStore.get("")
    override suspend fun refreshToken() = spotifyTokenStore.fresh("").token

    private val spotifyTokenStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            SpotifyAuthMapper.map(service.login())
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = {
                dao.getAuthToken(AuthTokenType.Spotify.value)
            },
            writer = { _: String, token ->
                dao.forceInsert(token)
            }
        )
    ).build()

    override suspend fun getAuthToken() = getToken().token
}
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
import de.schnettler.scrobbler.network.spotify.api.SpotifySearchService
import javax.inject.Inject

class SpotifyAuthProviderImpl @Inject constructor(
    private val service: SpotifyLoginService,
    private val dao: AuthDao
) : SpotifyAuthProvider {
    suspend fun getToken() = spotifyTokenStore.get("")
    suspend fun refreshToken() = spotifyTokenStore.fresh("")

    private val spotifyTokenStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            SpotifyAuthMapper.map(service.login(SpotifySearchService.TYPE_CLIENT))
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
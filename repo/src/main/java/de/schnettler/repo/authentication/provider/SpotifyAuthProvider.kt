package de.schnettler.repo.authentication.provider

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.AuthTokenType
import de.schnettler.lastfm.api.spotify.SpotifyAuthService
import de.schnettler.lastfm.api.spotify.SpotifyService
import de.schnettler.repo.mapping.SpotifyAuthMapper
import javax.inject.Inject

class SpotifyAuthProvider @Inject constructor(
    private val service: SpotifyAuthService,
    private val dao: AuthDao
) {
    suspend fun getToken() = spotifyTokenStore.get("")
    suspend fun refreshToken() = spotifyTokenStore.fresh("")

    private val spotifyTokenStore = StoreBuilder.from(
        fetcher = Fetcher.of { _: String ->
            SpotifyAuthMapper.map(service.login(SpotifyService.TYPE_CLIENT))
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { _: String ->
                dao.getAuthToken(AuthTokenType.Spotify.value)
            },
            writer = { _: String, token ->
                dao.forceInsert(token)
            }
        )
    ).build()
}
package de.schnettler.repo.authentication.provider

import com.dropbox.android.external.store4.*
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.models.AuthTokenType
import de.schnettler.lastfm.api.SpotifyService
import de.schnettler.repo.mapping.SpotifyAuthMapper

class SpotifyAuthProvider(private val spotifyAuthService: SpotifyService, private val authDao: AuthDao) {
    suspend fun getToken() = spotifyTokenStore.get("")
    suspend fun refreshToken() =  spotifyTokenStore.fresh("")

    private val spotifyTokenStore = StoreBuilder.from (
        fetcher = nonFlowValueFetcher {_: String ->
            SpotifyAuthMapper.map(spotifyAuthService.login(SpotifyService.TYPE_CLIENT))
        },
        sourceOfTruth = SourceOfTruth.from(
            reader = { _: String ->
                authDao.getAuthToken(AuthTokenType.Spotify.value)
            },
            writer = { _: String, token ->
                authDao.forceInsert(token)
            }
        )
    ).build()
}
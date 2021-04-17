package de.schnettler.repo.mapping.auth

import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.AuthTokenType
import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.network.spotify.models.SpotifyTokenDto

object SpotifyAuthMapper : Mapper<SpotifyTokenDto, AuthToken> {
    override suspend fun map(from: SpotifyTokenDto) = AuthToken(
        type = from.type,
        token = from.accessToken,
        tokenType = AuthTokenType.Spotify.value,
        validTill = System.currentTimeMillis() + 3600000
    )
}
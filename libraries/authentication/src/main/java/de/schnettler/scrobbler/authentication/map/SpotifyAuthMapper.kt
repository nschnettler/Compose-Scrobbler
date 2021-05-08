package de.schnettler.scrobbler.authentication.map

import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.network.spotify.models.SpotifyTokenDto
import de.schnettler.scrobbler.authentication.model.AuthToken
import de.schnettler.scrobbler.authentication.model.AuthTokenType

object SpotifyAuthMapper : Mapper<SpotifyTokenDto, AuthToken> {
    override suspend fun map(from: SpotifyTokenDto) = AuthToken(
        type = from.type,
        token = from.accessToken,
        tokenType = AuthTokenType.Spotify.value,
        validTill = System.currentTimeMillis() + 3600000
    )
}
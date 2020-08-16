package de.schnettler.repo.mapping.auth

import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.AuthTokenType
import de.schnettler.lastfm.models.SpotifyTokenDto
import de.schnettler.repo.mapping.Mapper

object SpotifyAuthMapper : Mapper<SpotifyTokenDto, AuthToken> {
    override suspend fun map(from: SpotifyTokenDto) = AuthToken(
        type = from.type,
        token = from.accessToken,
        tokenType = AuthTokenType.Spotify.value,
        validTill = System.currentTimeMillis() + 3600000
    )
}
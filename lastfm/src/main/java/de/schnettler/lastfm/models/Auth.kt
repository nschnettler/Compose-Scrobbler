package de.schnettler.lastfm.models

data class SpotifyAccessTokenDto(
    val access_token: String,
    val token_type: String,
    val expires_in: Long
)
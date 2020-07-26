package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class SpotifyTokenDto(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val type: String,
    @Json(name = "expires_in") val expiresIn: Long
)

data class SessionDto(
    val name: String,
    val key: String,
    val subscriber: Long
)
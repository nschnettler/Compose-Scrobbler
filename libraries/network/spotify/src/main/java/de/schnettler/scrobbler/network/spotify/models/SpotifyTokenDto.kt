package de.schnettler.scrobbler.network.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyTokenDto(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val type: String,
    @Json(name = "expires_in") val expiresIn: Long
)
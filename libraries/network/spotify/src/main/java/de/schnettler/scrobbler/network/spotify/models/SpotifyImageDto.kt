package de.schnettler.scrobbler.network.spotify.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyImageDto(
    val height: Long,
    val width: Long,
    val url: String
)
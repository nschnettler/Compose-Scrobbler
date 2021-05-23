package de.schnettler.scrobbler.image.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyImageDto(
    val height: Long,
    val width: Long,
    val url: String
)
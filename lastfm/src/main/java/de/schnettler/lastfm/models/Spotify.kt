package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyArtist(
    val images: List<SpotifyImageDto>,
    val popularity: Long
)

@JsonClass(generateAdapter = true)
data class SpotifyImageDto(
    val height: Long,
    val width: Long,
    val url: String
)
package de.schnettler.scrobbler.image.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyArtist(
    val images: List<SpotifyImageDto>,
    val popularity: Long
)
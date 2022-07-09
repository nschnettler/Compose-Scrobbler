package de.schnettler.scrobbler.image.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyArtist(
    val id: String,
    val name: String,
    val popularity: Long,
    val images: List<SpotifyImageDto>,
)
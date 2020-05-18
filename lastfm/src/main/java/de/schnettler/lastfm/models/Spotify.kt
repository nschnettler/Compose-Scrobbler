package de.schnettler.lastfm.models

data class SpotifyArtist(
    val images: List<SpotifyImageDto>,
    val popularity: Long
)

data class SpotifyImageDto(
    val height: Long,
    val width: Long,
    val url: String
)
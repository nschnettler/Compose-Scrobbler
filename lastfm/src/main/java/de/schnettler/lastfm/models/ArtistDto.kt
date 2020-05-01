package de.schnettler.lastfm.models

import com.squareup.moshi.Json


data class ArtistDto(
    val name: String,
    val playcount: Long,
    val listeners: Long,
    val mbid: String,
    val url: String,
    val streamable: String,
    @Json(name = "image") val images: List<ImageDto>
)

data class ImageDto(
    @Json(name = "#text") val url: String,
    val size: String
)

data class TopArtistsResponse(
    @Json(name = "artists") val wrapper: TopArtists
)

data class TopArtists(
    @Json(name = "artist") val artists: List<ArtistDto>
)
package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class UserAlbumDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val playcount: Long,
    val artist: MinimalListing,
    @Json(name = "image") val images: List<ImageDto>
): ListingDto

data class AlbumDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val playcount: Long,
    val artist: MinimalListing,
    @Json(name = "image") val images: List<ImageDto>
): ListingDto
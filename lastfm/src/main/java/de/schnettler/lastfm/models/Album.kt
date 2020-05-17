package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class AlbumDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val playcount: Long,
    val artist: ListingDto,
    @Json(name = "image") val images: List<ImageDto>
): ListingDto(name, mbid, url)
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

data class AlbumInfoDto(
    val name: String,
    val artist: String,
    val url: String,
    val image: List<ImageDto>,
    val listeners: Long,
    val playcount: Long,
    val userplaycount: Long = 0,
    val tracks: AlbumTracksDto,
    val tagsDto: TagsDto?,
    val wiki: WikiDto?
)

data class AlbumTracksDto(
    val track: List<AlbumTrack>
)

data class WikiDto(
    val published: String,
    val summary: String
)
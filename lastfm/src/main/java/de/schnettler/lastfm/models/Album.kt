package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class AlbumDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val playcount: Long,
    val artist: MinimalListing,
    @Json(name = "image") val images: List<ImageDto>
) : ListingDto

data class SearchResultDto(
    val name: String,
    val artist: String = "Unknown Artist",
    val url: String,
    val listeners: Long = 0
)

data class AlbumInfoDto(
    val name: String,
    val artist: String,
    val url: String,
    val image: List<ImageDto>,
    val listeners: Long,
    val playcount: Long,
    val userplaycount: Long = 0,
    val tracks: AlbumTracksDto,
    val tags: TagsDto,
    val wiki: WikiDto?
)

data class AlbumTracksDto(
    val track: List<AlbumTrack>
)

data class WikiDto(
    val published: String,
    val summary: String
)
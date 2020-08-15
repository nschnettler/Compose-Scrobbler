package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val playcount: Long,
    val artist: MinimalListing,
    @Json(name = "image") val images: List<ImageDto>
) : ListingDto

@JsonClass(generateAdapter = true)
data class SearchResultDto(
    val name: String,
    val artist: String = "Unknown Artist",
    val url: String,
    val listeners: Long = 0
)

@JsonClass(generateAdapter = true)
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

@JsonClass(generateAdapter = true)
data class AlbumTracksDto(
    val track: List<AlbumTrack>
)

@JsonClass(generateAdapter = true)
data class WikiDto(
    val published: String,
    val summary: String,
    val content: String?
)
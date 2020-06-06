package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class UserTrackDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val duration: Long,
    val artist: MinimalListing,
    val playcount: Long
): ListingDto

data class RecentTracksDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val artist: TrackRelationDto,
    val album: TrackRelationDto
): ListingDto

data class ArtistTracksDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val listeners: Long,
    val playcount: Long,
    val artist: MinimalListing
): ListingDto

data class TrackInfoDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val duration: Long,
    val listeners: Long,
    val playcount: Long,
    val artist: MinimalListing,
    val album: TrackAlbum?,
    val userplaycount: Long?,
    val userloved: Long,
    val toptags: TagsDto
): ListingDto

data class TrackRelationDto(
    @Json(name = "#text") val name: String,
    val mbid: String
)

data class TrackAlbum(
    val title: String
)

data class ImageDto(
    val size: String,
    @Json(name = "#text") val url: String
)

data class LovedTracksResponse(
    val track: List<UserTrackDto>,
    @Json(name = "@attr") val info: ResponseInfo
)
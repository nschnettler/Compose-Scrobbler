package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class UserTrackDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val playcount: Long?,
    val listeners: Long?,
    val artist: MinimalListing
): ListingDto

data class TrackWithAlbumDto(
    override val name: String,
    override val mbid: String,
    override val url: String,
    val artist: TrackRelationDto,
    val album: TrackRelationDto
): ListingDto

data class TrackRelationDto(
    @Json(name = "#text") val name: String,
    val mbid: String
)

data class ImageDto(
    val size: String,
    @Json(name = "#text") val url: String
)

data class LovedTracksResponse(
    val track: List<UserTrackDto>,
    @Json(name = "@attr") val info: ResponseInfo
)
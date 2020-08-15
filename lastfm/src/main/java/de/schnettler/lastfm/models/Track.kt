package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserTrackDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val duration: Long,
    val artist: MinimalListing,
    val playcount: Long
) : ListingDto

@JsonClass(generateAdapter = true)
data class RecentTracksDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val artist: TrackRelationDto,
    val album: TrackRelationDto,
    val date: TrackDateDto?,
    @Json(name = "@attr") val attrs: AttributesDto?
) : ListingDto

@JsonClass(generateAdapter = true)
data class AlbumTrack(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val duration: Long,
    val artist: TrackArtistDto
) : ListingDto

@JsonClass(generateAdapter = true)
data class AttributesDto(
    val nowplaying: String
)

@JsonClass(generateAdapter = true)
data class TrackDateDto(
    val uts: Long
)

@JsonClass(generateAdapter = true)
data class ArtistTracksDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val listeners: Long,
    val playcount: Long,
    val artist: MinimalListing
) : ListingDto

@JsonClass(generateAdapter = true)
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
    val toptags: TagsDto,
    val wiki: WikiDto?
) : ListingDto

@JsonClass(generateAdapter = true)
data class TrackRelationDto(
    @Json(name = "#text") val name: String,
    val mbid: String
)

@JsonClass(generateAdapter = true)
data class TrackAlbum(
    val title: String,
    val artist: String,
    val url: String,
    val image: List<ImageDto>
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    val size: String,
    @Json(name = "#text") val url: String
)

@JsonClass(generateAdapter = true)
data class MutlipleScrobblesResponse(
    @Json(name = "@attr") override val status: StatusResponse,
    val scrobble: List<ScrobbleResponse>
) : GeneralScrobbleResponse

@JsonClass(generateAdapter = true)
data class SingleScrobbleResponse(
    @Json(name = "@attr") override val status: StatusResponse,
    val scrobble: ScrobbleResponse
) : GeneralScrobbleResponse

interface GeneralScrobbleResponse {
    val status: StatusResponse
}

@JsonClass(generateAdapter = true)
data class StatusResponse(
    val accepted: Int,
    val ignored: Int
)

@JsonClass(generateAdapter = true)
data class ScrobbleResponse(
    val artist: CorrectionResponse,
    val album: CorrectionResponse,
    val albumArtist: CorrectionResponse,
    val track: CorrectionResponse,
    val ignoredMessage: IgnoredResponse
)

@JsonClass(generateAdapter = true)
data class CorrectionResponse(
    val corrected: String,
    @Json(name = "#text") val correctValue: String
)

@JsonClass(generateAdapter = true)
data class IgnoredResponse(
    val code: Long,
    @Json(name = "#text") val reason: String
)
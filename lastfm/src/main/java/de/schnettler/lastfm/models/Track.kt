package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class UserTrackDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val duration: Long,
    val artist: MinimalListing,
    val playcount: Long
) : ListingDto

data class RecentTracksDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val artist: TrackRelationDto,
    val album: TrackRelationDto,
    val date: TrackDateDto?,
    @Json(name = "@attr") val attrs: AttributesDto?
) : ListingDto

data class AlbumTrack(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val duration: Long,
    val artist: TrackArtistDto
) : ListingDto

data class AttributesDto(
    val nowplaying: String
)

data class TrackDateDto(
    val uts: Long
) {
    fun asMs() = uts * 1000
}

data class ArtistTracksDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val listeners: Long,
    val playcount: Long,
    val artist: MinimalListing
) : ListingDto

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

data class MutlipleScrobblesResponse(
    @Json(name = "@attr") override val status: StatusResponse,
    val scrobble: List<ScrobbleResponse>
) : GeneralScrobbleResponse

data class SingleScrobbleResponse(
    @Json(name = "@attr") override val status: StatusResponse,
    val scrobble: ScrobbleResponse
) : GeneralScrobbleResponse

interface GeneralScrobbleResponse {
    val status: StatusResponse
}

data class StatusResponse(
    val accepted: Int,
    val ignored: Int
)

data class ScrobbleResponse(
    val artist: CorrectionResponse,
    val album: CorrectionResponse,
    val albumArtist: CorrectionResponse,
    val track: CorrectionResponse,
    val ignoredMessage: IgnoredResponse
)

data class CorrectionResponse(
    val corrected: String,
    @Json(name = "#text") val correctValue: String
)

data class IgnoredResponse(
    val code: Long,
    @Json(name = "#text") val reason: String
)
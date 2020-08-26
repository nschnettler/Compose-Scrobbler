package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class BaseTrackDto(
    open val name: String,
    open val url: String,
    open val artist: MinimalArtist,
    open val album: TrackAlbum?
)

@JsonClass(generateAdapter = true)
data class UserTrackDto(
    override val name: String,
    override val url: String,
    override val artist: MinimalArtist,

    val mbid: String?,
    val duration: Long,
    val playcount: Long
) : BaseTrackDto(name, url, artist, null)

@JsonClass(generateAdapter = true)
data class RecentTracksDto(
    override val name: String,
    val mbid: String?,
    override val url: String,
    @Json(name = "artist") val artistResponse: TrackRelationDto,
    @Json(name = "album") val albumResponse: TrackRelationDto,
    val date: TrackDateDto?,
    @Json(name = "@attr") val attrs: AttributesDto?
) : BaseTrackDto(name, url, MinimalArtist(artistResponse.name, ""), null)

@JsonClass(generateAdapter = true)
data class AlbumTrack(
    override val name: String,
    override val url: String,
    override val artist: MinimalArtist,

    override val duration: Long,

    val mbid: String?,
) : BaseTrackDto(name, url, artist, null), BaseInfoDto

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
    override val url: String,
    override val artist: MinimalArtist,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = 0,

    val mbid: String?,
) : BaseTrackDto(name, url, artist, null), BaseStatsDto

@JsonClass(generateAdapter = true)
data class TrackInfoDto(
    override val name: String,
    override val url: String,
    override val artist: MinimalArtist,
    override val album: TrackAlbum?,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = 0,

    override val duration: Long,
    override val wiki: WikiDto?,

    val mbid: String?,
    override val userloved: Long,
    val toptags: TagsDto,
) : BaseTrackDto(name, url, artist, album), BaseStatsDto, BaseInfoDto

@JsonClass(generateAdapter = true)
data class TrackRelationDto(
    @Json(name = "#text") val name: String,
    val mbid: String
)

@JsonClass(generateAdapter = true)
data class AlbumTracksDto(
    val track: List<AlbumTrack>
)
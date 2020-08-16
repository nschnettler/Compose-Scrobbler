package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface BaseAlbumDto {
    val name: String
    val url: String
    val artist: String
    val image: List<ImageDto>
}

@JsonClass(generateAdapter = true)
open class BaseAlbumDtoImpl(
    override val name: String,
    override val url: String,
    override val artist: String,
    override val image: List<ImageDto>,
) : BaseAlbumDto

@JsonClass(generateAdapter = true)
data class AlbumDto(
    override val name: String,
    override val url: String,

    override val playcount: Long,
    override val listeners: Long = 0,
    override val userplaycount: Long = 0,

    val mbid: String?,
    @Json(name = "artist") val artistEntity: MinimalArtist,
    @Json(name = "image") val images: List<ImageDto>
) : BaseAlbumDtoImpl(name = name, artist = artistEntity.name, url = url, image = images), BaseStatsDto

@JsonClass(generateAdapter = true)
data class AlbumInfoDto(
    override val name: String,
    override val artist: String,
    override val url: String,
    override val image: List<ImageDto>,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = 0,

    override val tags: TagsDto,
    override val wiki: WikiDto?,
    override val duration: Long = 0, // Not part of Json. Will always be 0

    val tracks: AlbumTracksDto,
) : BaseAlbumDto, BaseStatsDto, BaseInfoDto

@JsonClass(generateAdapter = true)
data class TrackAlbum(
    @Json(name = "title") override val name: String,
    override val artist: String,
    override val url: String,
    override val image: List<ImageDto>
) : BaseAlbumDto
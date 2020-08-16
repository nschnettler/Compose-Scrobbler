package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface BaseAlbumDto {
    val name: String
    val url: String
    val artist: String
    val image: List<ImageDto>
}

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
) : BaseAlbumDtoImpl(name, artistEntity.name, url, images), BaseStatsDto

@JsonClass(generateAdapter = true)
data class AlbumInfoDto(
    override val name: String,
    override val artist: String,
    override val url: String,
    override val image: List<ImageDto>,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = 0,

    val tracks: AlbumTracksDto,
    val tags: TagsDto,
    val wiki: WikiDto?
) : BaseAlbumDto, BaseStatsDto

@JsonClass(generateAdapter = true)
data class TrackAlbum(
    @Json(name = "title") override val name: String,
    override val artist: String,
    override val url: String,
    override val image: List<ImageDto>
) : BaseAlbumDto
package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.AlbumResponse
import de.schnettler.scrobbler.model.remote.ImageResponse
import de.schnettler.scrobbler.model.remote.StatsResponse

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
    @Json(name = "image") override val images: List<ImageResponse>,

    override val playcount: Long,
    override val listeners: Long = -1,
    override val userplaycount: Long = -1,

    val mbid: String?,
    @Json(name = "artist") val artistEntity: MinimalArtist,
) : AlbumResponse, StatsResponse {
    override val artist: String = artistEntity.name
}

@JsonClass(generateAdapter = true)
data class TrackAlbum(
    @Json(name = "title") override val name: String,
    override val artist: String,
    override val url: String,
    override val image: List<ImageDto>
) : BaseAlbumDto
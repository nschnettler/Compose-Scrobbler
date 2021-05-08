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
data class TrackAlbum(
    @Json(name = "title") override val name: String,
    override val artist: String,
    override val url: String,
    override val image: List<ImageDto>
) : BaseAlbumDto
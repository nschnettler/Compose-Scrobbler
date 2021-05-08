package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.ArtistResponse

interface BaseArtistDto {
    val name: String
    val url: String
}

@JsonClass(generateAdapter = true)
data class MinimalArtist(
    override val name: String,
    override val url: String
) : ArtistResponse

@JsonClass(generateAdapter = true)
data class ChartArtistDto(
    override val name: String,
    override val url: String,

    val mbid: String?,
    val playcount: Long?,
    val listeners: Long?
) : ArtistResponse

@JsonClass(generateAdapter = true)
data class UserArtistDto(
    override val name: String,
    override val url: String,

    val mbid: String?,
    val playcount: Long?
) : ArtistResponse

@JsonClass(generateAdapter = true)
data class UserArtistResponse(
    val artist: List<UserArtistDto>,
    @Json(name = "@attr") val info: ResponseInfo
)

@JsonClass(generateAdapter = true)
data class SimilarArtistsDto(
    val artist: List<MinimalArtist>
)
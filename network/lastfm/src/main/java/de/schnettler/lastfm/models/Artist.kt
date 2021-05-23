package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface BaseArtistDto {
    val name: String
    val url: String
}

@JsonClass(generateAdapter = true)
data class MinimalArtist(
    override val name: String,
    override val url: String
) : BaseArtistDto

@JsonClass(generateAdapter = true)
data class ChartArtistDto(
    override val name: String,
    override val url: String,

    val mbid: String?,
    val playcount: Long?,
    val listeners: Long?
) : BaseArtistDto

@JsonClass(generateAdapter = true)
data class UserArtistDto(
    override val name: String,
    override val url: String,

    val mbid: String?,
    val playcount: Long?
) : BaseArtistDto

@JsonClass(generateAdapter = true)
data class UserArtistResponse(
    val artist: List<UserArtistDto>,
    @Json(name = "@attr") val info: ResponseInfo
)

@JsonClass(generateAdapter = true)
data class ArtistInfoDto(
    override val name: String,
    override val url: String,

    override val tags: TagsDto,
    @Json(name = "bio") override val wiki: WikiDto,

    val mbid: String?,
    val similar: SimilarArtistsDto,
    val stats: StatsDto
) : BaseArtistDto, BaseInfoDto

@JsonClass(generateAdapter = true)
data class SimilarArtistsDto(
    val artist: List<MinimalArtist>
)
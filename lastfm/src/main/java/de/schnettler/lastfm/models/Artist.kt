package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface BaseArtistDto : ListingDto {
    override val name: String
    override val mbid: String?
    override val url: String
    val playcount: Long?
}

@JsonClass(generateAdapter = true)
data class ChartArtistDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    override val playcount: Long?,
    val listeners: Long?
) : BaseArtistDto

@JsonClass(generateAdapter = true)
data class UserArtistDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    override val playcount: Long?
) : BaseArtistDto

@JsonClass(generateAdapter = true)
data class UserArtistResponse(
    val artist: List<UserArtistDto>,
    @Json(name = "@attr") val info: ResponseInfo
)

@JsonClass(generateAdapter = true)
data class ArtistInfoDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val bio: BioDto,
    val similar: SimilarArtistsDto,
    val tags: TagsDto,
    val stats: StatsDto
) : ListingDto

@JsonClass(generateAdapter = true)
data class TrackArtistDto(
    val name: String,
    val url: String
)

@JsonClass(generateAdapter = true)
data class BioDto(
    val published: String,
    val summary: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class SimilarArtistsDto(
    val artist: List<MinimalListing>
)
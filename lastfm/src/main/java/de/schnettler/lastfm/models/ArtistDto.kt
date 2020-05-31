package de.schnettler.lastfm.models

import com.squareup.moshi.Json

interface BaseArtistDto: ListingDto {
    override val name: String
    override val mbid: String?
    override val url: String
    val playcount: Long?
}

data class ChartArtistDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    override val playcount: Long?,
    val listeners: Long?
): BaseArtistDto

data class UserArtistDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    override val playcount: Long?
): BaseArtistDto


data class UserArtistResponse(
    val artist: List<UserArtistDto>,
    @Json(name = "@attr") val info: ResponseInfo
)

data class ArtistInfoDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val bio: BioDto,
    val similar: SimilarArtistsDto,
    val tags: TagsDto,
    val stats: StatsDto
): ListingDto

data class BioDto(
    val published: String,
    val summary: String,
    val content: String
)

data class SimilarArtistsDto(
    val artist: List<MinimalListing>
)
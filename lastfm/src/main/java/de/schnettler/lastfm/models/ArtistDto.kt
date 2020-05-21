package de.schnettler.lastfm.models

data class ArtistDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val playcount: Long?,
    val listeners: Long?,
    val streamable: String?
): ListingDto(name, mbid, url)

data class ArtistInfoDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val bio: BioDto,
    val similar: ArtistListDto,
    val tags: TagsDto,
    val stats: StatsDto
): ListingDto(name, mbid, url)

data class BioDto(
    val published: String,
    val summary: String,
    val content: String
)

data class ArtistListDto(
    val artist: List<ArtistDto>
)

data class TagsDto(
    val tag: List<ListingDto>
)

data class StatsDto(
    val listeners: Long,
    val playcount: Long
)
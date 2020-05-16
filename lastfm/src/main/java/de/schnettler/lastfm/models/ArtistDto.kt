package de.schnettler.lastfm.models

data class ArtistDto(
    override val name: String,
    override val mbid: String,
    override val url: String,
    val playcount: Long,
    val listeners: Long?,
    val streamable: String
): ListingDto(name, mbid, url)

data class ArtistInfoDto(
    override val name: String,
    override val mbid: String?,
    override val url: String,
    val bio: BioDto,
    val similar: ArtistListDto,
    val tags: TagsDto
): ListingDto(name, mbid, url)

data class BioDto(
    val published: String,
    val summary: String,
    val content: String
)

data class ArtistListDto(
    val artist: List<ListingDto>
)

data class TagsDto(
    val tag: List<ListingDto>
)
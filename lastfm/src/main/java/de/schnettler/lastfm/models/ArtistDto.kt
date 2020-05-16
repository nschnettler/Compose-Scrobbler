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
    val bio: BioDto
): ListingDto(name, mbid, url)

data class BioDto(
    val published: String,
    val summary: String,
    val content: String
)
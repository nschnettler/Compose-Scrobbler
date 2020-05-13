package de.schnettler.lastfm.models

data class AlbumDto(
    override val name: String,
    override val mbid: String,
    override val url: String,
    val playcount: Long,
    val artist: ListingDto
): ListingDto(name, mbid, url)
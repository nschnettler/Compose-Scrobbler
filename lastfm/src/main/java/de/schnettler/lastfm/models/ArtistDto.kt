package de.schnettler.lastfm.models



data class ArtistDto(
    val name: String,
    val playcount: Long,
    val listeners: Long,
    val mbid: String,
    val url: String,
    val streamable: String
)
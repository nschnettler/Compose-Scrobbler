package de.schnettler.lastfm.models

data class ArtistDto(
    val name: String,
    val playcount: Long,
    val listeners: Long,
    val mbid: String,
    val url: String,
    val streamable: String
)

data class SessionDto(
    val name: String,
    val key: String,
    val subscriber: Long
)
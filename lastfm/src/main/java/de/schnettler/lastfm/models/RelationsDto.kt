package de.schnettler.lastfm.models

data class RelationDto(
    val type: String,
    val url: UrlDt
)

data class UrlDt(
    val resource: String
)
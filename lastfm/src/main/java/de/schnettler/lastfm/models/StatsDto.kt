package de.schnettler.lastfm.models

data class StatsDto(
    val listeners: Long,
    val playcount: Long,
    val userplaycount: Long?
)
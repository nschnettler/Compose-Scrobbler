package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatsDto(
    val listeners: Long,
    val playcount: Long,
    val userplaycount: Long?
)
package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass

interface BaseStatsDto {
    val listeners: Long
    val playcount: Long
    val userplaycount: Long
}

@JsonClass(generateAdapter = true)
data class StatsDto(
    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = 0
) : BaseStatsDto
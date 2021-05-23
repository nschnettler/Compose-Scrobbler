package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.StatsResponse

@JsonClass(generateAdapter = true)
data class DetailStatsResponse(
    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = -1
) : StatsResponse
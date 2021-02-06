package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusResponse(
    val accepted: Int,
    val ignored: Int
)

@JsonClass(generateAdapter = true)
data class CorrectionResponse(
    val corrected: Long,
    @Json(name = "#text") val correctValue: String
)

@JsonClass(generateAdapter = true)
data class IgnoredResponse(
    val code: Long,
    @Json(name = "#text") val reason: String
)
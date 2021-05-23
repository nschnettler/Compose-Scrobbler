package de.schnettler.scrobbler.submission.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IgnoredResponse(
    val code: Long,
    @Json(name = "#text") val reason: String
)
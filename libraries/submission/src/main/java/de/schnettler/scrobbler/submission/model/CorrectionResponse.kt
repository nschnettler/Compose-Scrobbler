package de.schnettler.scrobbler.submission.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CorrectionResponse(
    val corrected: Long,
    @Json(name = "#text") val correctValue: String
)
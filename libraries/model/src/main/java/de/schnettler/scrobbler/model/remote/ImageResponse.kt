package de.schnettler.scrobbler.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageResponse(
    val size: String,
    @Json(name = "#text") val url: String
)
package de.schnettler.scrobbler.profile.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DateResponse(
    @Json(name = "#text") val unixtime: Long
)
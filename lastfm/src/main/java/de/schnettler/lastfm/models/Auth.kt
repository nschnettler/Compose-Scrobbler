package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionDto(
    val name: String,
    val key: String,
    val subscriber: Long
)
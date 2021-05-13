package de.schnettler.scrobbler.authentication.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionResponse(
    val name: String,
    val key: String,
    val subscriber: Long
)
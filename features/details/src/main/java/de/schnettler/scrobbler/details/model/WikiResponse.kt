package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WikiResponse(
    val published: String,
    val summary: String,
    val content: String?
)
package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WikiDto(
    val published: String,
    val summary: String,
    val content: String?
)
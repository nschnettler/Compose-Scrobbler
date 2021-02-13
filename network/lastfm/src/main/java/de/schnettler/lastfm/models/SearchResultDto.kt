package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultDto(
    val name: String,
    val artist: String = "Unknown Artist",
    val url: String,
    val listeners: Long = 0
)
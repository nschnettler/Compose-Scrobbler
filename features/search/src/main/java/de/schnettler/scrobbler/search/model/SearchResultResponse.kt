package de.schnettler.scrobbler.search.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultResponse(
    val name: String,
    val artist: String = "Unknown Artist",
    val url: String,
    val listeners: Long = 0
)
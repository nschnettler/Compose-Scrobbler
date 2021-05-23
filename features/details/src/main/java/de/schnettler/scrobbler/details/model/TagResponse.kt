package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TagResponse(
    val name: String,
    val mbid: String? = null,
    val url: String
)
package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TagListResponse(
    val tag: List<TagResponse>
)
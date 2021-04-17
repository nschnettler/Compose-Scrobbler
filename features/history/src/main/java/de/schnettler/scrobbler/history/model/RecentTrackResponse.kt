package de.schnettler.scrobbler.history.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecentTrackResponse(
    val name: String,
    val mbid: String?,
    val url: String,
    val artist: RelatedResponse,
    val album: RelatedResponse,
    val date: TrackDateResponse?,
    @Json(name = "@attr") val attrs: AttributesResponse?
)

@JsonClass(generateAdapter = true)
data class RelatedResponse(
    @Json(name = "#text") val name: String,
    val mbid: String
)

@JsonClass(generateAdapter = true)
data class TrackDateResponse(
    val uts: Long
)

@JsonClass(generateAdapter = true)
data class AttributesResponse(
    val nowplaying: String
)
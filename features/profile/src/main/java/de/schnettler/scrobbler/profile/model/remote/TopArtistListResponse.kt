package de.schnettler.scrobbler.profile.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopArtistListResponse(
    val artist: List<TopArtistResponse>,
    @Json(name = "@attr") val info: ResponseInfo
)
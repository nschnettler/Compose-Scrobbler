package de.schnettler.scrobbler.details.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.AlbumResponse
import de.schnettler.scrobbler.model.remote.ImageResponse

@JsonClass(generateAdapter = true)
data class TrackAlbumResponse(
    @Json(name = "title") override val name: String,
    override val artist: String,
    override val url: String,
    @Json(name = "image") override val images: List<ImageResponse>
) : AlbumResponse
package de.schnettler.scrobbler.profile.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.AlbumResponse
import de.schnettler.scrobbler.model.remote.ImageResponse
import de.schnettler.scrobbler.model.remote.MinArtistResponse
import de.schnettler.scrobbler.model.remote.StatsResponse

@JsonClass(generateAdapter = true)
data class TopAlbumResponse(
    override val name: String,
    override val url: String,
    @Json(name = "image") override val images: List<ImageResponse>,

    override val playcount: Long,
    override val listeners: Long = -1,
    override val userplaycount: Long = -1,

    val mbid: String?,
    @Json(name = "artist") val artistEntity: MinArtistResponse,
) : AlbumResponse, StatsResponse {
    override val artist: String = artistEntity.name
}
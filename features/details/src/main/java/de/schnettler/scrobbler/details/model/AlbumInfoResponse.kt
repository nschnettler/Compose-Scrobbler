package de.schnettler.scrobbler.details.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.AlbumResponse
import de.schnettler.scrobbler.model.remote.ImageResponse
import de.schnettler.scrobbler.model.remote.StatsResponse

@JsonClass(generateAdapter = true)
data class AlbumInfoResponse(
    override val name: String,
    override val artist: String,
    override val url: String,
    @Json(name = "image") override val images: List<ImageResponse>,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = -1,

    override val tags: TagListResponse,
    override val wiki: WikiResponse?,
    override val duration: Long = 0, // Not part of Json. Will always be 0

    val tracks: AlbumTrackListResponse,
) : AlbumResponse, StatsResponse, InfoResponse
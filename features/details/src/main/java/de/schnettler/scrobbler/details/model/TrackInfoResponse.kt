package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.StatsResponse
import de.schnettler.scrobbler.model.remote.TrackResponse

@JsonClass(generateAdapter = true)
data class TrackInfoResponse(
    override val name: String,
    override val url: String,
    override val artist: MinArtistResponse,
    val album: TrackAlbumResponse?,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = -1,

    // Duration is in ms
    override val duration: Long,
    override val wiki: WikiResponse?,

    val mbid: String?,
    override val userloved: Long = 0,
    val toptags: TagListResponse,
) : TrackResponse, InfoResponse, StatsResponse
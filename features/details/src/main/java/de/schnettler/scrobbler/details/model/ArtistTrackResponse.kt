package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.MinArtistResponse
import de.schnettler.scrobbler.model.remote.StatsResponse
import de.schnettler.scrobbler.model.remote.TrackResponse

@JsonClass(generateAdapter = true)
data class ArtistTrackResponse(
    override val name: String,
    override val url: String,
    override val artist: MinArtistResponse,

    override val listeners: Long,
    override val playcount: Long,
    override val userplaycount: Long = 0,

    val mbid: String?,
) : StatsResponse, TrackResponse
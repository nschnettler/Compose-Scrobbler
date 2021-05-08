package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.TrackResponse

@JsonClass(generateAdapter = true)
data class AlbumTrackResponse(
    override val name: String,
    override val url: String,
    override val artist: MinArtistResponse,

    override val duration: Long,

    val mbid: String?,
) : TrackResponse, InfoResponse
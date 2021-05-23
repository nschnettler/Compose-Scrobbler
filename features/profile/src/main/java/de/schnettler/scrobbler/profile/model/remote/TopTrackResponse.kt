package de.schnettler.scrobbler.profile.model.remote

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.MinArtistResponse
import de.schnettler.scrobbler.model.remote.TrackResponse

@JsonClass(generateAdapter = true)
data class TopTrackResponse(
    override val name: String,
    override val url: String,
    override val artist: MinArtistResponse,

    val mbid: String?,
    val duration: Long,
    val playcount: Long
) : TrackResponse
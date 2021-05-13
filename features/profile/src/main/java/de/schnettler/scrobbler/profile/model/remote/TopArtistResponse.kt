package de.schnettler.scrobbler.profile.model.remote

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.ArtistResponse

@JsonClass(generateAdapter = true)
data class TopArtistResponse(
    override val name: String,
    override val url: String,

    val mbid: String?,
    val playcount: Long?
) : ArtistResponse
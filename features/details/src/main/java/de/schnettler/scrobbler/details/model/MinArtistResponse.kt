package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.ArtistResponse

@JsonClass(generateAdapter = true)
data class MinArtistResponse(
    override val name: String,
    override val url: String
) : ArtistResponse
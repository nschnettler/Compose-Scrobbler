package de.schnettler.scrobbler.model.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MinArtistResponse(
    override val name: String,
    override val url: String
) : ArtistResponse
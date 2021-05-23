package de.schnettler.scrobbler.details.model

import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.MinArtistResponse

@JsonClass(generateAdapter = true)
data class SimilarArtistListResponse(
    val artist: List<MinArtistResponse>
)
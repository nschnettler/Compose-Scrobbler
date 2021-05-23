package de.schnettler.scrobbler.details.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.ArtistResponse

@JsonClass(generateAdapter = true)
data class ArtistInfoResponse(
    override val name: String,
    override val url: String,

    override val tags: TagListResponse,
    @Json(name = "bio") override val wiki: WikiResponse,

    val mbid: String?,
    val similar: SimilarArtistListResponse,
    val stats: DetailStatsResponse
) : ArtistResponse, InfoResponse
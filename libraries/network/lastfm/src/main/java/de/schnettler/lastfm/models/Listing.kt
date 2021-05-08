package de.schnettler.lastfm.models

import com.squareup.moshi.JsonClass

interface ListingDto {
    val name: String
    val mbid: String?
    val url: String
}

@JsonClass(generateAdapter = true)
data class MinimalListing(
    override val name: String,
    override val mbid: String? = null,
    override val url: String
) : ListingDto


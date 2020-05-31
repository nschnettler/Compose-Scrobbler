package de.schnettler.lastfm.models

interface ListingDto {
    val name: String
    val mbid: String?
    val url: String
}

data class MinimalListing(
    override val name: String,
    override val mbid: String? = null,
    override val url: String
): ListingDto

data class ResponseInfo(
    val page: Long,
    val perPage: Long,
    val user: String,
    val total: Long,
    val totalPages: Long
)
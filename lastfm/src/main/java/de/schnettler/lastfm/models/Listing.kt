package de.schnettler.lastfm.models

open class ListingDto(
    open val name: String,
    open val mbid: String?,
    open val url: String
)

data class ResponseInfo(
    val page: Long,
    val perPage: Long,
    val user: String,
    val total: Long,
    val totalPages: Long
)
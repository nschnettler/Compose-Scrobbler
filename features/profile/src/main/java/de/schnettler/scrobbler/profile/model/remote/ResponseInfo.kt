package de.schnettler.scrobbler.profile.model.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseInfo(
    val page: Long,
    val perPage: Long,
    val user: String,
    val total: Long,
    val totalPages: Long
)
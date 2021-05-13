package de.schnettler.scrobbler.submission.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusResponse(
    val accepted: Int,
    val ignored: Int
)
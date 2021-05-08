package de.schnettler.scrobbler.details.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.ImageResponse

@JsonClass(generateAdapter = true)
data class ImageResponse(
    override val size: String,
    @Json(name = "#text") override val url: String
) : ImageResponse
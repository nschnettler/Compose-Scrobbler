package de.schnettler.scrobbler.profile.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.schnettler.scrobbler.model.remote.ImageResponse

@JsonClass(generateAdapter = true)
data class UserInfoResponse(
    val name: String,
    val url: String,
    val playcount: Long,
    val country: String,
    val age: Long,
    val realname: String,
    @Json(name = "registered") val registerDate: DateResponse,
    val image: List<ImageResponse>
)
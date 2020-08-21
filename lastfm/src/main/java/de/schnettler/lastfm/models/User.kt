package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val name: String,
    val url: String,
    val playcount: Long,
    val country: String,
    val age: Long,
    val realname: String,
    @Json(name = "registered") val registerDate: DateDto,
    val image: List<ImageDto>
)

@JsonClass(generateAdapter = true)
data class DateDto(
    @Json(name = "#text") val unixtime: Long
)
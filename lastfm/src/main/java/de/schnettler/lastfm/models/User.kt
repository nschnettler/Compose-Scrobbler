package de.schnettler.lastfm.models

import com.squareup.moshi.Json

data class UserDto(
    override val name: String,
    override val url: String,
    val playcount: Long,
    val country: String,
    val age: Long,
    val realname: String,
    @Json(name = "registered") val registerDate: DateDto
): ListingDto(name, null, url)


data class DateDto(
    @Json(name = "#text") val unixtime: Long
)
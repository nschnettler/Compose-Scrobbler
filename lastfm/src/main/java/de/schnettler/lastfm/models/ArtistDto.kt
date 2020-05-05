package de.schnettler.lastfm.models

import com.squareup.moshi.Json
import com.sun.org.apache.xpath.internal.operations.Bool

data class ArtistDto(
    val name: String,
    val playcount: Long,
    val listeners: Long?,
    val mbid: String,
    val url: String,
    val streamable: String
)

data class SessionDto(
    val name: String,
    val key: String,
    val subscriber: Long
)

data class UserDto(
   val name: String,
   val playcount: Long,
   val url: String,
   val country: String,
   val age: Long,
   val realname: String,
   @Json(name = "registered") val registerDate: DateDto
)

data class DateDto(
    @Json(name = "#text") val unixtime: Long
)

data class TrackDto(
    val artist: TrackRelationDto,
    val album: TrackRelationDto,
    val mbid: String,
    val url: String,
    val name: String
)

data class TrackRelationDto(
    @Json(name = "#text") val name: String,
    val mbid: String
)
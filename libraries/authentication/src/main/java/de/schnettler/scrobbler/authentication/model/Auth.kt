package de.schnettler.scrobbler.authentication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Generic Authentication Result in Database
 */
@Entity(tableName = "auth")
data class AuthToken(
    @PrimaryKey val tokenType: String,
    val token: String,
    val type: String,
    val validTill: Long
)

enum class AuthTokenType(val value: String) {
    Spotify("spotify_token")
}
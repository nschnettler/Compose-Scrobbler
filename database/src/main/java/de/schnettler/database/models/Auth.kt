package de.schnettler.database.models

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
) {
    fun isValid() = remainingMinutes() > 0
    fun remainingMinutes() = (validTill - System.currentTimeMillis()) / 60000
}

enum class AuthTokenType(val value: String) {
    Spotify("spotify_token")
}
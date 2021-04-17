package de.schnettler.scrobbler.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val name: String,
    val playcount: Long,
    val url: String,
    val age: Long,
    val realname: String,
    val registerDate: Long,
    val countryCode: String,
    val imageUrl: String,
    var artistCount: Long = 0,
    var lovedTracksCount: Long = 0
)
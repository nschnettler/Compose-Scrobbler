package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Track(
    @PrimaryKey val id: String,
    val name: String,
    val artist: String,
    val album: String
)
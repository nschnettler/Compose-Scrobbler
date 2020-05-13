package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey val name: String,
    val playcount: Long,
    val mbid: String,
    val url: String,
    val artist: String
)

data class TopListEntry(
    val name: String,
    val playcount: Long
)
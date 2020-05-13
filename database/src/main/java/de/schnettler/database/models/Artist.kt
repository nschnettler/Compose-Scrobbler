package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Artist(
    @PrimaryKey val name: String,
    val playcount: Long,
    val listeners: Long,
    val mbid: String,
    val url: String,
    val streamable: String
): TopListEntry(name, playcount)
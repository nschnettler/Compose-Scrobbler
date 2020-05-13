package de.schnettler.database.models

import androidx.room.PrimaryKey

data class Track(
    @PrimaryKey val id: String,
    val name: String,
    val artist: String,
    val album: String? = null,
    val playcount: Long = 0
): TopListEntry(name, playcount)
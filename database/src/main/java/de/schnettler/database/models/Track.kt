package de.schnettler.database.models

import androidx.room.PrimaryKey

data class Track(
    val id: String?,
    @PrimaryKey val name: String,
    val artist: String,
    val album: String? = null,
    val playcount: Long = 0,
    val listener: Long = 0
): Listing(name, playcount.toString())
package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    override val url: String,
    val duration: Long = 0,
    override val listeners: Long = 0,
    override val plays: Long = 0,
    val artist: String,
    val album: String? = null,
    override val userPlays: Long = 0,
    val userLoved: Boolean = false,
    val tags: List<String> = listOf(),
    override val imageUrl: String? = null
): ListingMin
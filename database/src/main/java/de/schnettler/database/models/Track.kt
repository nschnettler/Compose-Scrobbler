package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "tracks")
data class Track(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    val trackId: String?,
    override val url: String,
    override val plays: Long = 0,
    override val listeners: Long = 0,
    override val imageUrl: String? = null,
    val artist: String?,
    val album: String? = null
): ListingMin

data class ListEntryWithTrack(
    @Embedded val listing: ListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "name"
    )
    val track: Track
)
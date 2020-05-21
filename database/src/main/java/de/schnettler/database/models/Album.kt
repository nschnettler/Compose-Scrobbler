package de.schnettler.database.models

import androidx.room.*

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey override val name: String,
    override val url: String,
    override val plays: Long = 0,
    override var imageUrl: String? = null,
    override val listeners: Long = 0,
    val artist: String?
): ListingMin


data class ListEntryWithAlbum(
    @Embedded val listing: ListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "name"
    )
    val album: Album
)
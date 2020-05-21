package de.schnettler.database.models

import androidx.room.*

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey override val name: String,
    override val url: String,
    override val plays: Long = 0,
    override val listeners: Long = 0,
    override var imageUrl: String? = null,
    val bio: String? = null,
    val tags: List<String> = listOf()
): ListingMin {
    @Ignore var similarArtists: List<ListingMin> = listOf()
    @Ignore var topAlbums: List<ListingMin> = listOf()
    @Ignore var topTracks: List<ListingMin> = listOf()
}

data class ListEntryWithArtist(
    @Embedded val listing: ListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "name"
    )
    val artist: Artist
)
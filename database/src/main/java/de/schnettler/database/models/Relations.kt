package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import de.schnettler.database.models.LastFmEntity.Artist

@Entity(tableName = "artist_relations", primaryKeys = ["artistId", "orderIndex"])
data class RelatedArtistEntry(
    val artistId: String,
    val otherArtistId: String,
    val orderIndex: Int
)

enum class ListingType(val id: Int) {
    UNDEFINED(-1),
    ALBUM(0),
    ARTIST(1),
    TRACK(2)
}

data class RelatedArtist(
    @Embedded val relation: RelatedArtistEntry,
    @Relation(parentColumn = "otherArtistId", entityColumn = "id")
    val artist: Artist
)
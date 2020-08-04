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

data class RelatedArtist(
    @Embedded val relation: RelatedArtistEntry,
    @Relation(parentColumn = "otherArtistId", entityColumn = "id")
    val artist: Artist
)
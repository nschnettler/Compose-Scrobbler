package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "relations", primaryKeys = ["sourceId", "sourceType", "targetType", "index"])
data class RelationEntity(
    val sourceId: String,
    val sourceType: ListingType,
    val index: Int,
    val targetId: String,
    val targetType: ListingType
)

enum class ListingType(val id: Int) {
    UNDEFINED(-1),
    ALBUM(0),
    ARTIST(1),
    TRACK(2)
}

data class RelatedAlbum(
    @Embedded val relation: RelationEntity,
    @Relation(
        parentColumn = "targetId",
        entityColumn = "id"
    )
    val album: Album
)

data class RelatedTrack(
    @Embedded val relation: RelationEntity,
    @Relation(
        parentColumn = "targetId",
        entityColumn = "id"
    )
    val track: Track
)

data class RelatedArtist(
    @Embedded val relation: RelationEntity,
    @Relation(
        parentColumn = "targetId",
        entityColumn = "id"
    )
    val artist: Artist
)
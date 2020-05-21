package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "table_relations", primaryKeys = ["sourceName", "sourceType", "index"])
data class RelationEntity(
    val sourceName: String,
    val sourceType: ListingType,
    val index: Int,
    val targetName: String,
    val targetType: ListingType
)

enum class ListingType(val id: Int){
    UNDEFINED(-1),
    ALBUM(0),
    ARTIST(1),
    TRACK(2)
}

data class RelatedAlbum(
    @Embedded val relation: RelationEntity,
    @Relation(
        parentColumn = "targetName",
        entityColumn = "name"
    )
    val album: Album
)

data class RelatedTrack(
    @Embedded val relation: RelationEntity,
    @Relation(
        parentColumn = "targetName",
        entityColumn = "name"
    )
    val track: Track
)
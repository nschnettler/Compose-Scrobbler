package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "table_charts", primaryKeys = ["type", "index"])
data class ListEntry(
    val type: String,
    val index: Int,
    val id: String
)

data class ListEntryWithArtist(
    @Embedded val listing: ListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "name"
    )
    val artist: Artist
)
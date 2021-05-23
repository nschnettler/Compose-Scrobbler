package de.schnettler.database.models

import androidx.room.Entity

@Entity(tableName = "toplist", primaryKeys = ["entityType", "listType", "index"])
data class TopListEntry(
    val id: String,
    val entityType: EntityType,
    val listType: ListType,
    val index: Int,
    val count: Long
)

enum class EntityType {
    ALBUM,
    ARTIST,
    TRACK,
    UNDEF
}

enum class ListType {
    USER,
    CHART,
    UNDEF
}

interface Toplist {
    val listing: TopListEntry
    val value: LastFmEntity
}
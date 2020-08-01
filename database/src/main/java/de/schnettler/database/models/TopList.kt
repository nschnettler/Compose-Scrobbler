package de.schnettler.database.models

import androidx.room.Entity

@Entity(tableName = "toplist", primaryKeys = ["type", "index"])
data class TopListEntry(
    val id: String,
    val type: EntityType,
    val index: Int,
    val count: Long
)

enum class EntityType {
    ALBUM,
    ARTIST,
    TRACK,
    UNDEF
}

interface Toplist {
    val listing: TopListEntry
    val value: LastFmEntity
}
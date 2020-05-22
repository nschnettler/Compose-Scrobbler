package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "charts", primaryKeys = ["type", "index"])
data class TopListEntry(
    val id: String,
    val type: TopListEntryType,
    val index: Int
)

enum class TopListEntryType(val id: String) {
    UNDEFINED("undef"),
    USER_ALBUM("usr_album"),
    USER_ARTIST("usr_artist"),
    USER_TRACKS("usr_tracks"),
    CHART_ARTIST("chart_artist")
}

data class TopListArtist(
    @Embedded val topListEntry: TopListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val artist: Artist
)
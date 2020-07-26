package de.schnettler.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "charts", primaryKeys = ["type", "index"])
data class TopListEntry(
    val id: String,
    val type: TopListEntryType,
    val index: Int,
    val count: Long
)

enum class TopListEntryType(val id: String) {
    UNDEFINED("undef"),
    USER_ALBUM("usr_album"),
    USER_ARTIST("usr_artist"),
    USER_TRACKS("usr_tracks"),
    CHART_ARTIST("chart_artist")
}

data class TopListArtist(
    @Embedded override val topListEntry: TopListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    override val data: Artist
) : TopListEntryWithData

data class TopListTrack(
    @Embedded override val topListEntry: TopListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    override val data: Track
) : TopListEntryWithData

data class TopListAlbum(
    @Embedded override val topListEntry: TopListEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    override val data: Album
) : TopListEntryWithData

interface TopListEntryWithData {
    val topListEntry: TopListEntry
    val data: LastFmStatsEntity
}
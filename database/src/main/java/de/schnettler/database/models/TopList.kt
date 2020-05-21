package de.schnettler.database.models

import androidx.room.Entity

@Entity(tableName = "table_charts", primaryKeys = ["type", "index"])
data class ListEntry(
    val type: String,
    val index: Int,
    val id: String
)

enum class ChartType(val type: String) {
    USER_ALBUM("usr_album"),
    USER_ARTIST("usr_artist"),
    USER_TRACKS("usr_tracks")
}
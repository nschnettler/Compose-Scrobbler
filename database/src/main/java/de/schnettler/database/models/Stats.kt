package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class Stats(
    @PrimaryKey val id: String,
    val plays: Long = -1,
    val listeners: Long = -1,
    val userPlays: Long = -1
)
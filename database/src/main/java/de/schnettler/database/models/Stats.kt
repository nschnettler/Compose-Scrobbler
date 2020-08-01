package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class Stats(
    @PrimaryKey val id: String,
    val plays: Long = 0,
    val listeners: Long = 0,
    val userPlays: Long = 0
)
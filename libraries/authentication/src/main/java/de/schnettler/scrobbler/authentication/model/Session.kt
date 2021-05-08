package de.schnettler.scrobbler.authentication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey val name: String,
    val key: String,
    val date: Long
)
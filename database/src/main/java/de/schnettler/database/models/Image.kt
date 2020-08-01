package de.schnettler.database.models

import androidx.room.Entity

@Entity(tableName = "images")
data class Image(
    val id: String,
    val url: String
)
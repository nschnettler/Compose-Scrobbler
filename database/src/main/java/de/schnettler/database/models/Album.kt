package de.schnettler.database.models

import androidx.room.*

@Entity(tableName = "albums")
data class Album(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    override val url: String,
    override val plays: Long = 0,
    override var imageUrl: String? = null,
    override val listeners: Long = 0,
    val artist: String?
): ListingMin
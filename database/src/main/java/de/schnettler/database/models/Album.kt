package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey val name: String,
    val playcount: Long,
    val mbid: String,
    val url: String,
    val artist: String,
    override val imageUrl: String? = null
): TopListEntry(name, playcount, imageUrl)

open class TopListEntry(
    @Ignore val title: String,
    @Ignore val plays: Long,
    @Ignore open val imageUrl: String? = null
)
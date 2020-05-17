package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey val name: String,
    val playcount: Long,
    val mbid: String?,
    val url: String,
    val artist: String,
    override val imageUrl: String? = null
): Listing(name, playcount.toString(), imageUrl)

open class Listing(
    @Ignore val title: String,
    @Ignore val subtitle: String? = null,
    @Ignore open val imageUrl: String? = null
)
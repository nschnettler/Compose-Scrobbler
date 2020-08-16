package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Locale

interface BaseEntity

@Entity(tableName = "entity_info")
data class EntityInfo(
    @PrimaryKey val id: String,
    val tags: List<String> = listOf(),
    val duration: Long = 0,
    val wiki: String?
)

sealed class LastFmEntity(
    @Ignore open val id: String,
    @Ignore open val name: String,
    @Ignore open val url: String,
    @Ignore open val imageUrl: String?
) : BaseEntity {

    @Entity(tableName = "albums")
    data class Album(
        override val name: String,
        override val url: String,
        val artist: String,
        @PrimaryKey
        override val id: String = "album_${name.toLowerCase(Locale.US)}:artist_${artist.toLowerCase(Locale.US)}",
        override val imageUrl: String? = null
    ) : LastFmEntity(id, name, url, imageUrl)

    @Entity(tableName = "artists")
    data class Artist(
        override val name: String,
        override val url: String,
        @PrimaryKey override val id: String = "artist_${name.toLowerCase(Locale.US)}",
        override val imageUrl: String? = null
    ) : LastFmEntity(id, name, url, imageUrl)

    @Entity(tableName = "tracks")
    data class Track(
        override val name: String,
        override val url: String,
        val artist: String,
        val album: String? = null,
        val albumId: String? = null,
        @PrimaryKey
        override val id: String = "track_${name.toLowerCase(Locale.US)}:artist_${artist.toLowerCase(Locale.US)}",
        override val imageUrl: String? = null,
    ) : LastFmEntity(id, name, url, imageUrl)
}
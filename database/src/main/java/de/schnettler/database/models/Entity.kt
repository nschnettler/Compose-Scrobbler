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
    val wiki: String
)

sealed class LastFmEntity(
    @Ignore open val id: String,
    @Ignore open val name: String,
    @Ignore open val url: String
) : BaseEntity {

    @Entity(tableName = "albums")
    data class Album(
        override val name: String,
        override val url: String,
        val artist: String,
        @PrimaryKey override val id: String = "album_${name.toLowerCase(Locale.US)}:${artist.toLowerCase(Locale.US)}"
    ) : LastFmEntity(id, name, url) {
        @Ignore var tracks: List<Track> = listOf()
//    fun getLength() = TimeUnit.SECONDS.toMinutes(tracks.sumByLong { it.duration })
    }

    @Entity(tableName = "artists")
    data class Artist(
        override val name: String,
        override val url: String,
        @PrimaryKey override val id: String = "artist_$name"
    ) : LastFmEntity(id, name, url)

    @Entity(tableName = "tracks")
    data class Track(
        override val name: String,
        override val url: String,
        val artist: String,
        val album: String? = null,
        @PrimaryKey override val id: String = "track_$name:$artist"
    ) : LastFmEntity(id, name, url)
}
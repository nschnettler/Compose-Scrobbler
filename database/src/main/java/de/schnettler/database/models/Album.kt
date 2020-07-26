package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.schnettler.database.sumByLong
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlbumWithoutArtistException(message: String) : Exception(message)

@Entity(tableName = "albums")
data class Album(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(Locale.US),
    override val url: String,
    override val plays: Long = 0,
    override val userPlays: Long = 0,
    override var imageUrl: String? = null,
    override val listeners: Long = 0,
    val artist: String?,
    val tags: List<String> = listOf(),
    val description: String? = null
) : LastFmStatsEntity {
    fun getArtistOrThrow() = artist
        ?: throw AlbumWithoutArtistException("Album is expected to have an artist at this point")

    @Ignore
    var tracks: List<Track> = listOf()
    fun getLength() = TimeUnit.SECONDS.toMinutes(tracks.sumByLong { it.duration })
}
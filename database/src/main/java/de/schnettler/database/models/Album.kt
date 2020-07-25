package de.schnettler.database.models

import androidx.room.*
import de.schnettler.database.sumByLong
import java.util.concurrent.TimeUnit

@Entity(tableName = "albums")
data class Album(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    override val url: String,
    override val plays: Long = 0,
    override val userPlays: Long = 0,
    override var imageUrl: String? = null,
    override val listeners: Long = 0,
    val artist: String?,
    val tags: List<String> = listOf(),
    val description: String? = null
): LastFmStatsEntity {
    fun getArtistOrThrow() = artist ?: throw Exception("Artist missing")
    @Ignore var tracks: List<Track> = listOf()
    fun getLength() = TimeUnit.SECONDS.toMinutes(tracks.sumByLong { it.duration })
}
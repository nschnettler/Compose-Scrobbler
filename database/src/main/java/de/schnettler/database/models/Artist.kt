package de.schnettler.database.models

import androidx.room.*

@Entity(tableName = "artists")
data class Artist(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    override val url: String,
    override val plays: Long = 0,
    override val listeners: Long = 0,
    override var imageUrl: String? = null,
    val bio: String? = null,
    val tags: List<String> = listOf()
): ListingMin {
    @Ignore var similarArtists: List<Artist> = listOf()
    @Ignore var topAlbums: List<Album> = listOf()
    @Ignore var topTracks: List<Track> = listOf()
}
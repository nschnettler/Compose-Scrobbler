package de.schnettler.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Artist(
    @PrimaryKey val name: String,
    val playcount: Long,
    val listeners: Long,
    val mbid: String,
    val url: String,
    val streamable: String,
    override var imageUrl: String? = null
): Listing(name, playcount.toString(), imageUrl)

data class ArtistMin(
    val name: String,
    val url: String
): Listing(name)

data class ArtistInfo(
    @PrimaryKey val name: String,
    val bio: String,
    val similar: List<ArtistMin>,
    val tags: List<String>,
    val listeners: Long,
    val playcount: Long,
    var topAlbums: List<Album> = listOf(),
    var topTracks: List<Track> = listOf()
)
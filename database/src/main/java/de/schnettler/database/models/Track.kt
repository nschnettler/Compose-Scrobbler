package de.schnettler.database.models

import androidx.room.*

@Entity(tableName = "tracks")
data class Track(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    override val url: String,
    val duration: Long = 0,
    override val listeners: Long = 0,
    override val plays: Long = 0,
    val artist: String,
    val album: String? = null,
    override val userPlays: Long = 0,
    val userLoved: Boolean = false,
    val tags: List<String> = listOf(),
    override var imageUrl: String? = null
): ListingMin

data class TrackWithAlbum(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "album",
        entityColumn = "name"
    ) val album: Album?
)

data class TrackDomain(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    override val url: String,
    val duration: Long = 0,
    override val listeners: Long = 0,
    override val plays: Long = 0,
    val artist: String,
    val album: Album?,
    override val userPlays: Long = 0,
    val userLoved: Boolean = false,
    val tags: List<String> = listOf(),
    override var imageUrl: String? = null
): ListingMin

@Entity(tableName = "localTracks")
data class LocalTrack(
        @PrimaryKey(autoGenerate = true)  var id: Long? = null,
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long
) {
    fun isTheSameAs(other: LocalTrack?) = title == other?.title && artist == other.artist
    fun canBeScrobbled() = duration > 30000
}
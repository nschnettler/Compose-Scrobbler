package de.schnettler.database.models

import android.text.format.DateUtils
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
): ListingMin {
    @Ignore var timestamp: Long = 0
    @Ignore var scrobbleStatus: ScrobbleStatus = ScrobbleStatus.VOLATILE
    fun isPlaying() = scrobbleStatus == ScrobbleStatus.PLAYING
    fun timestampToRelativeTime() =
        if (timestamp > 0) {
            DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils
                .MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString()
        } else null
}

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

@Entity(tableName = "localTracks", primaryKeys = ["startTime", "playedBy"])
data class LocalTrack(
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long,

        val startTime: Long = System.currentTimeMillis(),
        val endTime: Long = System.currentTimeMillis(),
        var amountPlayed: Long = 0,
        val playedBy: String,
        var status: ScrobbleStatus = ScrobbleStatus.VOLATILE
) {
    @Ignore var trackingStart: Long = startTime
    fun isTheSameAs(other: LocalTrack?) = title == other?.title && artist == other.artist
    private fun canBeScrobbled() = duration > 30000
    private fun playedEnough() = amountPlayed >= (duration / 2)
    fun readyToScrobble() = canBeScrobbled() && playedEnough()
    fun isPlaying() = status == ScrobbleStatus.PLAYING

    fun pause() {
        updateAmountPlayed()
        status = ScrobbleStatus.PAUSED
    }

    private fun updateAmountPlayed() {
        if (!isPlaying()) return
        val now = System.currentTimeMillis()
        amountPlayed =  now - trackingStart
        trackingStart = now
    }

    fun play() {
        if (!isPlaying()) trackingStart = System.currentTimeMillis()
        status = ScrobbleStatus.PLAYING
    }
}

enum class ScrobbleStatus {
    LOCAL,
    PLAYING,
    PAUSED,
    SCROBBLED,
    VOLATILE
}
package de.schnettler.database.models

import android.text.format.DateUtils
import androidx.room.*
import kotlin.math.roundToInt

@Entity(tableName = "tracks")
data class Track(
    override val name: String,
    @PrimaryKey override val id: String = name.toLowerCase(),
    override val url: String,
    override val duration: Long = 0,
    override val listeners: Long = 0,
    override val plays: Long = 0,
    override val artist: String,
    override val album: String? = null,
    override val userPlays: Long = 0,
    val userLoved: Boolean = false,
    val tags: List<String> = listOf(),
    override var imageUrl: String? = null,
    val rank: Int = -1
): LastFmStatsEntity, StatusTrack {
    @Ignore override var timestamp: Long = 0
    @Ignore override var status: ScrobbleStatus = ScrobbleStatus.VOLATILE
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
): LastFmStatsEntity

@Entity(tableName = "localTracks", primaryKeys = ["timestamp", "playedBy"])
data class LocalTrack(
        override val name: String,
        override val artist: String,
        override val album: String,
        override val duration: Long,

        override val timestamp: Long = System.currentTimeMillis(),
        val endTime: Long = System.currentTimeMillis(),
        var amountPlayed: Long = 0,
        val playedBy: String,
        override var status: ScrobbleStatus = ScrobbleStatus.VOLATILE,
        var trackingStart: Long = timestamp
): StatusTrack {
    private fun playedEnough() = amountPlayed >= (duration / 2)
    fun readyToScrobble() = canBeScrobbled() && playedEnough()
    fun playPercent() = (amountPlayed.toFloat() / duration * 100).roundToInt()
    fun timeStampUnix() = (timestamp / 1000).toString()
    fun durationUnix() = (duration / 1000).toString()

    fun pause() {
        updateAmountPlayed()
        status = ScrobbleStatus.PAUSED
    }

    private fun updateAmountPlayed() {
        if (!isPlaying()) return
        val now = System.currentTimeMillis()
        amountPlayed +=  now - trackingStart
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

interface CommonTrack: CommonEntity {
    val artist: String
    val album: String?
    val duration: Long
    fun isTheSameAs(track: CommonTrack?) = name == track?.name && artist == track.artist
    fun canBeScrobbled() = duration > 30000
}

interface StatusTrack: CommonTrack {
    val timestamp: Long
    var status: ScrobbleStatus

    fun isPlaying() = status == ScrobbleStatus.PLAYING
    fun isLocal() = status == ScrobbleStatus.LOCAL
    fun timestampToRelativeTime() =
            if (timestamp > 0) {
                DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils
                        .MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString()
            } else null
}
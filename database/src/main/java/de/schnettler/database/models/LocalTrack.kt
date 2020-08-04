package de.schnettler.database.models

import android.text.format.DateUtils
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Locale
import kotlin.math.roundToInt

@Suppress("TooManyFunctions")
@Entity(tableName = "localTracks")
data class LocalTrack(
    val name: String,
    val artist: String,
    val album: String,
    val duration: Long,

    @PrimaryKey val timestamp: Long = System.currentTimeMillis() / 1000,
    val endTime: Long = System.currentTimeMillis(),
    var amountPlayed: Long = 0,
    val playedBy: String,
    var status: ScrobbleStatus = ScrobbleStatus.VOLATILE,
    var trackingStart: Long = timestamp
) {
    @Ignore val id: String = name.toLowerCase(Locale.US)
    @Ignore val url: String = "https://www.last.fm/music/$artist/_/$name"
    private fun playedEnough() = amountPlayed >= (duration / 2)
    fun readyToScrobble() = canBeScrobbled() && playedEnough()
    fun playPercent() = (amountPlayed.toFloat() / duration * 100).roundToInt()
    fun timeStampString() = timestamp.toString()
    fun durationUnix() = (duration / 1000).toString()
    fun isPlaying() = status == ScrobbleStatus.PLAYING
    fun isLocal() = isCached() || status == ScrobbleStatus.SCROBBLED
    fun isCached() = status == ScrobbleStatus.LOCAL
    fun timestampToRelativeTime() =
        if (timestamp > 0) {
            DateUtils.getRelativeTimeSpanString(
                timestamp * 1000, System.currentTimeMillis(), DateUtils
                    .MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL
            ).toString()
        } else null
    fun isTheSameAs(track: LocalTrack?) = name == track?.name && artist == track.artist
    fun canBeScrobbled() = duration > 30000

    fun pause() {
        updateAmountPlayed()
        status = ScrobbleStatus.PAUSED
    }

    private fun updateAmountPlayed() {
        if (!isPlaying()) return
        val now = System.currentTimeMillis()
        amountPlayed += now - trackingStart
        trackingStart = now
    }

    fun play() {
        if (!isPlaying()) trackingStart = System.currentTimeMillis()
        status = ScrobbleStatus.PLAYING
    }

    fun asLastFmTrack() = LastFmEntity.Track(
        name = name,
        url = url,
        artist = artist,
        album = album
    )
}

enum class ScrobbleStatus {
    LOCAL,
    PLAYING,
    PAUSED,
    SCROBBLED,
    VOLATILE,
    EXTERNAL
}
package de.schnettler.scrobbler.service

import de.schnettler.database.models.ScrobbleTrack
import kotlin.math.roundToLong

data class PlaybackItem(
        val track: ScrobbleTrack,
        var playing: Boolean = false,
        var amountPlayed: Long = 0,
        var trackingStartTime: Long = 0,
        var timestamp: Long = 0,
        val playedBy: String
) {
    fun playPercentage() = (amountPlayed.toDouble() / track.duration * 100).roundToLong()

    fun playedEnough() = amountPlayed >= (track.duration / 2)

    fun stopPlaying() {
        updateAmountPlayed()
        playing = false
    }

    fun startPlaying() {
        if (!playing) {
            trackingStartTime = System.currentTimeMillis()
        }
        if (timestamp == 0L) {
            timestamp = System.currentTimeMillis()
        }
        playing = true
    }

    fun updateAmountPlayed() {
        if (!playing) return

        val now = System.currentTimeMillis()
        val start = trackingStartTime
        amountPlayed += now - start
        trackingStartTime = now
    }

    fun resetAmountPlayed() {
        amountPlayed = 0
    }
}
package de.schnettler.scrobbler.service

import de.schnettler.database.models.LocalTrack
import kotlin.math.roundToLong

data class PlaybackItem(
        var track: LocalTrack,
        var playing: Boolean = false,
        var amountPlayed: Long = 0,
        var playBackStartTime: Long = 0
) {
    fun playPercentage() = (amountPlayed.toDouble() / track.duration * 100).roundToLong()

    fun playedEnough() = amountPlayed >= (track.duration / 2)

    fun stopPlaying() {
        //TODO: Update amount played
        playing = false
    }

    fun startPlaying(startTime: Long? = null) {
        if (!playing) {
            playBackStartTime = startTime ?: System.currentTimeMillis()
        }
        playing = true
    }

    fun updateAmountPlayed() {
        if (!playing) return

        val now = System.currentTimeMillis()
        val start = playBackStartTime
        amountPlayed += now - start
        playBackStartTime = now
    }
}
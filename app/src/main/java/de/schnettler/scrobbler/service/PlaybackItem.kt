package de.schnettler.scrobbler.service

import kotlin.math.roundToLong

data class PlaybackItem(
        var track: Track,
        var playing: Boolean = false,
        var amountPlayed: Long = 0,
        var playBackStartTime: Long
) {
    fun playPercentage() = (amountPlayed.toDouble() / track.duration * 100).roundToLong()

    fun stopPlaying() {
        //TODO: Update amount played
        playing = false
    }

    fun startPlaying() {
        if (!playing) {
            playBackStartTime = System.currentTimeMillis()
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
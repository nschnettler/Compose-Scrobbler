package de.schnettler.scrobbler.service

import android.media.session.PlaybackState
import timber.log.Timber

class PlaybackState(private val player: String) {
    var playbackItem: PlaybackItem? = null

    fun updateTrack(track: Track) {
        var currentTrack: Track? = null
        var isPlaying = false
        val now = System.currentTimeMillis()

        playbackItem?.let {
            currentTrack = it.track
            isPlaying = it.playing
        }

        if (track == currentTrack) {        // Track didn't change
            playbackItem?.track = track
        } else {                            // Track changed
            playbackItem?.let {
                playbackItem?.stopPlaying()
                //TODO: Submit old track
                if (playbackItem?.amountPlayed ?: 0 > 0) {//IGNORE NOT PLAYED SONGS
                    Timber.d("[${player}] Saved PlayBackItem ($playbackItem), ${playbackItem?.playPercentage()} %")
                }
            }
            // use new track
            playbackItem = PlaybackItem(track = track, playBackStartTime = now)
        }

        if (isPlaying) {
            // TODO: Update now Playing
            playbackItem?.startPlaying()
        }
        Timber.d("[${player}] New PlayBackItem ($playbackItem)")
    }

    fun updatePlayBackState(playbackState: PlaybackState?) {
        if (playbackItem == null) return

        playbackItem?.updateAmountPlayed()

        if (playbackState?.state == PlaybackState.STATE_PLAYING) {
            playbackItem?.startPlaying()
            Timber.d("[${player}] Playing ($playbackItem)")
        } else {
            playbackItem?.stopPlaying()
            Timber.d("[${player}] Paused ($playbackItem), ${playbackItem?.playPercentage()} %")
        }
    }
}
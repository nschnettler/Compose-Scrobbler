package de.schnettler.scrobbler.service

import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import timber.log.Timber

class PlaybackController(private val controller: MediaController, private val scrobbler: Scrobbler) {
    var playbackItem: PlaybackItem? = null

    fun updateTrack(track: LocalTrack) {
        val now = System.currentTimeMillis()

        when(track.isTheSameAs(playbackItem?.track)) {
            // Track is the same (title and artist match)
            true -> {
                // Update Metadata
                playbackItem?.track = playbackItem?.track?.copy(album = track.album) ?: track
                Timber.d("[Update] $playbackItem")
            }

            // Track changed
            false -> {
                // 1. Save old Track
                playbackItem?.let {playbackItem ->
                    playbackItem.stopPlaying()
                    Timber.d("[Submit] $playbackItem, ${playbackItem.playPercentage()} %")
                    scrobbler.submitPlaybackItem(playbackItem)
                }
                // 2. Track the new Track
                playbackItem = PlaybackItem(track = track)
                if (controller.playbackState?.state == PlaybackState.STATE_PLAYING) playbackItem?.startPlaying(now)
                Timber.d("[New] $playbackItem")
            }
        }
    }

    fun updatePlayBackState(playbackState: PlaybackState?) {
        if (playbackItem == null) return

        playbackItem?.updateAmountPlayed()

        if (playbackState?.state == PlaybackState.STATE_PLAYING) {
            playbackItem?.startPlaying()
            Timber.d("[Play] $playbackItem")
        } else {
            playbackItem?.stopPlaying()
            Timber.d("[Pause] $playbackItem, ${playbackItem?.playPercentage()} %")
        }
    }
}
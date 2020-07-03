package de.schnettler.scrobbler.service

import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import timber.log.Timber

class PlaybackState(private val player: String, private val scrobbler: Scrobbler) {
    var playbackItem: PlaybackItem? = null

    fun updateTrack(track: LocalTrack) {
        val now = System.currentTimeMillis()
        val wasPlaying = playbackItem?.playing ?: false

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
                    if (playbackItem.playedEnough()) {
                        Timber.d("[Save] $playbackItem, ${playbackItem.playPercentage()} %")
                        scrobbler.saveTrack(playbackItem.track)
                    }
                }
                // 2. Track the new Track
                playbackItem = PlaybackItem(track = track)
                if (wasPlaying) playbackItem?.startPlaying(now)
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
            Timber.d("Pause] $playbackItem, ${playbackItem?.playPercentage()} %")
        }
    }
}
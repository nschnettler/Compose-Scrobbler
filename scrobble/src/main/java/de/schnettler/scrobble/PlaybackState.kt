package de.schnettler.scrobble

import android.media.session.PlaybackState
import de.schnettler.database.models.Scrobble
import de.schnettler.scrobble.util.isPlaying
import timber.log.Timber

class PlaybackState(
    private val scrobbler: Scrobbler
) {
    private var nowPlaying: Scrobble? = null
    private var lastPlaybackState: Int? = null

    fun updateTrack(track: Scrobble) {
        val wasPlaying = nowPlaying?.isPlaying() ?: true

        when (track.isTheSameAs(nowPlaying)) {
            // Track is the same (title and artist match)
            true -> {
                Timber.d("[Controller] Updated Track $track")
                nowPlaying = nowPlaying?.copy(album = track.album, duration = track.duration)
            }

            // Track changed
            false -> {
                // Save old Track
                nowPlaying?.let {
                    it.pause()
                    scrobbler.submitScrobble(it)
                }
                // Start new Track
                Timber.d("[Controller] New Track $track")
                nowPlaying = track
                if (wasPlaying) {
                    track.play()
                    scrobbler.notifyNowPlaying(nowPlaying)
                }
            }
        }
    }

    fun updatePlayBackState(playbackState: PlaybackState) {
        val current = nowPlaying ?: return
        if (playbackState.state == lastPlaybackState) return
        lastPlaybackState = playbackState.state

        if (playbackState.isPlaying()) {
            current.play()
            Timber.d("[Controller] Resumed")
        } else {
            current.pause()
            Timber.d("[Controller] Paused")
        }
    }
}
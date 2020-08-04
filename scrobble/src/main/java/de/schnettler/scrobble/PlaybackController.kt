package de.schnettler.scrobble

import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import timber.log.Timber

class PlaybackController(
    private val scrobbler: Scrobbler
) {
    var nowPlaying: LocalTrack? = null
    var lastPlaybackState: Int? = null

    fun updateTrack(track: LocalTrack) {
        val wasPlaying = nowPlaying?.isPlaying() ?: true

        when (track.isTheSameAs(nowPlaying)) {
            // Track is the same (title and artist match)
            true -> {
                Timber.d("[Controller] Updated Track $track")
                nowPlaying = nowPlaying?.copy(album = track.album, duration = track.duration)
            }

            // Track changed
            false -> {
                Timber.d("[Controller] Track Changed $track")
                // Save old Track
                nowPlaying?.let {
                    it.pause()
                    scrobbler.submitScrobble(it)
                }
                // Start new Track
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
            scrobbler.updateNowPlayingNotification(current)
        } else {
            current.pause()
            scrobbler.updateNowPlayingNotification(null)
        }
    }
}
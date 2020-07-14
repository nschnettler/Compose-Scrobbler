package de.schnettler.scrobble

import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack

class PlaybackController(
    private val scrobbler: Scrobbler
) {
    var nowPlaying: LocalTrack? = null
    var lastPlaybackState: Int? = null

    fun updateTrack(track: LocalTrack) {
        when(track.isTheSameAs(nowPlaying)) {
            // Track is the same (title and artist match)
            true -> {
                if (track.album != nowPlaying?.album) {
                    //Album updated. Metadata change complete
                    nowPlaying = nowPlaying?.copy(album = track.album)
                    scrobbler.notifyNowPlaying(nowPlaying)
                }
            }

            // Track changed
            false -> {
                // Save old Track
                nowPlaying?.let {
                    it.pause()
                    scrobbler.submitScrobble(it)
                }
                //Start new Track
                nowPlaying = track
                track.play()
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
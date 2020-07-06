package de.schnettler.scrobbler.service

import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import timber.log.Timber

class PlaybackController(private val controller: MediaController, private val scrobbler: Scrobbler) {

    var lastPlaybackState: Int? = null

    fun saveOldTrack(track: LocalTrack) {
        if (track.readyToScrobble()) {
            scrobbler.saveTrack(track.copy(status = ScrobbleStatus.LOCAL))
        } else {
            scrobbler.removeTrack(track)
        }
    }

    fun insertNowPlaying(track: LocalTrack) {
        scrobbler.saveTrack(track)
    }

    fun updateTrack(track: LocalTrack) {
        val currentTrack = scrobbler.currentTrack
        when(track.isTheSameAs(scrobbler.currentTrack)) {
            // Track is the same (title and artist match)
            true -> {
                // Update Metadata
                scrobbler.updateTrackAlbum(currentTrack, track.album)
                Timber.d("[Update] $currentTrack")
            }

            // Track changed
            false -> {
                // Save old Track
                currentTrack?.let {
                    it.pause()
                    saveOldTrack(it)
                    Timber.d("[Save] $currentTrack")
                }
                //Start new Track
                if (controller.isPlaying()) {
                    track.play()
                }
                insertNowPlaying(track)
                Timber.d("[New] $currentTrack")
            }
        }
    }

    fun updatePlayBackState(playbackState: PlaybackState) {
        val currentTrack = scrobbler.currentTrack ?: return
        if (playbackState.state == lastPlaybackState) return
        lastPlaybackState = playbackState.state

        if (playbackState.isPlaying()) {
            currentTrack.play()
            Timber.d("[Play] $currentTrack")
        } else {
            currentTrack.pause()
            Timber.d("[Pause] $currentTrack")
        }

        scrobbler.saveTrack(currentTrack)
    }
}

fun MediaController.isPlaying() = playbackState?.isPlaying() ?: false
fun PlaybackState.isPlaying() = state == PlaybackState.STATE_PLAYING
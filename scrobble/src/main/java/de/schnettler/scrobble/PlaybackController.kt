package de.schnettler.scrobble

import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.repo.ScrobbleRepository
import timber.log.Timber

class PlaybackController(
        private val controller: MediaController,
        private val repo: ScrobbleRepository,
        private val notificationManager: ScrobbleNotificationManager
) {

    var lastPlaybackState: Int? = null

    fun saveOldTrack(track: LocalTrack) {
        if (track.readyToScrobble()) {
            repo.saveTrack(track.copy(status = ScrobbleStatus.LOCAL))
        } else {
            repo.removeTrack(track)
        }
    }

    fun insertNowPlaying(track: LocalTrack) {
        repo.saveTrack(track)
    }

    fun updateTrack(track: LocalTrack) {
        val currentTrack = repo.currentTrack
        when(track.isTheSameAs(repo.currentTrack)) {
            // Track is the same (title and artist match)
            true -> {
                // Update Metadata
                repo.updateTrackAlbum(currentTrack, track.album)
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
                Timber.d("[New] $track")
                updateNowPlaying(track)
            }
        }
    }

    fun updatePlayBackState(playbackState: PlaybackState) {
        val currentTrack = repo.currentTrack ?: return
        if (playbackState.state == lastPlaybackState) return
        lastPlaybackState = playbackState.state

        if (playbackState.isPlaying()) {
            currentTrack.play()
            Timber.d("[Play] $currentTrack")
            updateNowPlaying(currentTrack)
        } else {
            currentTrack.pause()
            Timber.d("[Pause] $currentTrack")
            updateNowPlaying(null)
        }

        repo.saveTrack(currentTrack)
    }

    private fun updateNowPlaying(current: LocalTrack?) {
        if (current == null) {
            notificationManager.cancelNotifications(NOW_PLAYING_ID)
        } else {
            notificationManager.updateNowPlayingNotification(current)
        }
    }
}

fun MediaController.isPlaying() = playbackState?.isPlaying() ?: false
fun PlaybackState.isPlaying() = state == PlaybackState.STATE_PLAYING
package de.schnettler.scrobble

import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.models.Errors
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.ServiceCoroutineScope
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.LastFmPostResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaybackController(
        private val repo: ScrobbleRepository,
        private val notificationManager: ScrobbleNotificationManager,
        private val scope: ServiceCoroutineScope,
        private val authProvider: LastFmAuthProvider
) {
    var nowPlaying: LocalTrack? = null
    var lastPlaybackState: Int? = null

    private fun saveOldTrack(track: LocalTrack) {
        if (track.readyToScrobble()) {
            Timber.d("[Save] $nowPlaying")
            scope.launch {
                if (authProvider.loggedIn()) {
                    when(val result = repo.createAndSubmitScrobble(track)) {
                        is LastFmPostResponse.ERROR -> {
                            when(result.error) {
                                Errors.OFFLINE, Errors.UNAVAILABLE -> {
                                    //Cache Scrobble
                                    Timber.d("Scrobble failed. Service offline")
                                    repo.saveTrack(track.copy(status = ScrobbleStatus.LOCAL))
                                }
                                Errors.SESSION -> {
                                    //Reauth and retry
                                    Timber.d("Scrobble failed. Unauthorized")
                                    repo.saveTrack(track.copy(status = ScrobbleStatus.LOCAL))
                                }
                                else -> {
                                    //Skip this Scrobble
                                }
                            }
                        }
                        is LastFmPostResponse.SUCCESS<*> -> {
                            Timber.d("Scrobble successful")
                            notificationManager.scrobbledNotification(track)
                            repo.saveTrack(track.copy(status = ScrobbleStatus.SCROBBLED))
                        }
                    }
                }
            }
        } else {
            Timber.d("[Skip] $nowPlaying")
        }
    }

    private fun notifyNowPlaying(track: LocalTrack?) {
        updateNowPlaying(nowPlaying)
        Timber.d("[New] $nowPlaying")
        track?.let {
            scope.launch {
                if (authProvider.loggedIn()) {
                    val result = repo.submitNowPlaying(track)
                    handleResult(result)
                }
            }
        }
    }

    private fun handleResult(result: LastFmPostResponse) {
        when(result) {
            is LastFmPostResponse.ERROR -> Timber.e("${result.error}")
            is LastFmPostResponse.SUCCESS<*> -> Timber.d("${result.data}")
        }
    }

    fun updateTrack(track: LocalTrack) {
        when(track.isTheSameAs(nowPlaying)) {
            // Track is the same (title and artist match)
            true -> {
                if (track.album != nowPlaying?.album) {
                    //Album updated. Metadata change complete
                    nowPlaying = nowPlaying?.copy(album = track.album)
                    notifyNowPlaying(nowPlaying)
                }
            }

            // Track changed
            false -> {
                // Save old Track
                nowPlaying?.let {
                    it.pause()
                    saveOldTrack(it)
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
            //Timber.d("[Play] $current")
            updateNowPlaying(current)
        } else {
            current.pause()
            //Timber.d("[Pause] $current")
            updateNowPlaying(null)
        }
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
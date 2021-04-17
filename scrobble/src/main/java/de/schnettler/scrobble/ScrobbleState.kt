package de.schnettler.scrobble

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.scrobble.util.isPlaying
import de.schnettler.scrobbler.model.Scrobble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ScrobbleState(
    val controller: MediaController,
    private val scrobbler: Scrobbler,
    private val scope: CoroutineScope
) : MediaController.Callback() {

    private var nowPlaying: Scrobble? = null
    private var lastPlaybackState: Int? = null

    private fun updateTrack(track: Scrobble) {
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
                    scope.launch { scrobbler.submitScrobble(it) }
                }
                // Start new Track
                Timber.d("[Controller] New Track $track")
                nowPlaying = track
                if (controller.playbackState?.isPlaying() == true) {
                    track.play()
                    scrobbler.notifyNowPlaying(nowPlaying)
                }
            }
        }
    }

    private fun updatePlayBackState(playbackState: PlaybackState) {
        val current = nowPlaying ?: return
        if (playbackState.state == lastPlaybackState) return
        lastPlaybackState = playbackState.state

        if (playbackState.isPlaying()) {
            current.play()
            Timber.d("[Controller] Resumed")
        } else {
            current.pause()
            nowPlaying?.let {
                scope.launch {
                    if (scrobbler.submitScrobble(it)) {
                        nowPlaying?.amountPlayed = 0
                    }
                }
            }
            Timber.d("[Controller] Paused")
        }
    }

    override fun onMetadataChanged(metadata: MediaMetadata?) {
        metadata?.let { updateTrack(Scrobble.fromMetadata(metadata, controller.packageName)) }
    }

    override fun onPlaybackStateChanged(state: PlaybackState?) {
        state?.let { updatePlayBackState(state) }
    }
}
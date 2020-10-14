package de.schnettler.scrobble

import android.media.MediaMetadata
import android.media.session.PlaybackState
import de.schnettler.database.models.Scrobble
import javax.inject.Inject

class PlaybackTracker @Inject constructor(
    private val scrobbler: Scrobbler
) {
    private val playerStates = hashMapOf<String, de.schnettler.scrobble.PlaybackState>()

    private fun getPlaybackController(packageName: String) =
        playerStates[packageName] ?: PlaybackState(scrobbler).also {
            playerStates[packageName] = it
        }

    fun onMetadataChanged(packageName: String, metadata: MediaMetadata) {
        getPlaybackController(packageName).updateTrack(Scrobble.fromMetadata(metadata, packageName))
    }

    fun onStateChanged(packageName: String, state: PlaybackState) {
        getPlaybackController(packageName).updatePlayBackState(state)
    }
}
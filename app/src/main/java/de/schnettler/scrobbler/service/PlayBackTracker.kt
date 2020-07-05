package de.schnettler.scrobbler.service

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.database.models.ScrobbleTrack
import javax.inject.Inject

class PlayBackTracker @Inject constructor(private val scrobbler: Scrobbler) {
    private val playerStates = hashMapOf<MediaController, PlaybackController>()

    private fun getPlaybackController(controller: MediaController) =
            playerStates[controller] ?: PlaybackController(controller, scrobbler).also { playerStates[controller] = it }

    fun onMetadataChanged(controller: MediaController, metadata: MediaMetadata) {
        getPlaybackController(controller).updateTrack(track = ScrobbleTrack(
                title = metadata.getText(MediaMetadata.METADATA_KEY_TITLE).toString(),
                artist = (metadata.getText(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata.getText(MediaMetadata
                        .METADATA_KEY_ALBUM_ARTIST)).toString(),
                album = metadata.getText(MediaMetadata.METADATA_KEY_ALBUM).toString(),
                duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION))
        )
    }

    fun onStateChanged(controller: MediaController, state: PlaybackState) {
        getPlaybackController(controller).updatePlayBackState(state)
    }
}
package de.schnettler.scrobbler.service

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import javax.inject.Inject

class PlayBackTracker @Inject constructor(private val scrobbler: Scrobbler) {
    private val playerStates = hashMapOf<String, de.schnettler.scrobbler.service.PlaybackState>()

    private fun getPlayerState(player: String) =
            playerStates[player] ?: PlaybackState(player, scrobbler).also { playerStates[player] = it }

    fun onMetadataChanged(controller: MediaController, metadata: MediaMetadata) {
        getPlayerState(controller.packageName).updateTrack(track = LocalTrack(
                title = metadata.getText(MediaMetadata.METADATA_KEY_TITLE).toString(),
                artist = (metadata.getText(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata.getText(MediaMetadata
                        .METADATA_KEY_ALBUM_ARTIST)).toString(),
                album = metadata.getText(MediaMetadata.METADATA_KEY_ALBUM).toString(),
                duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION))
        )
    }

    fun onStateChanged(controller: MediaController, state: PlaybackState) {
        getPlayerState(controller.packageName).updatePlayBackState(state)
    }
}
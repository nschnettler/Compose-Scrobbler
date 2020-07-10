package de.schnettler.scrobble

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.ScrobbleRepository
import javax.inject.Inject

class PlayBackTracker @Inject constructor(
        private val repo: ScrobbleRepository,
        private val notificationManager: ScrobbleNotificationManager
) {
    private val playerStates = hashMapOf<MediaController, PlaybackController>()

    private fun getPlaybackController(controller: MediaController) =
            playerStates[controller] ?: PlaybackController(controller, repo, notificationManager).also { playerStates[controller] = it }

    fun onMetadataChanged(controller: MediaController, metadata: MediaMetadata) {
        val title = (metadata.getText(MediaMetadata.METADATA_KEY_TITLE) ?: "").toString()
        val artist = ((metadata.getText(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata.getText(MediaMetadata
                .METADATA_KEY_ALBUM_ARTIST)) ?: "").toString()
        val album = (metadata.getText(MediaMetadata.METADATA_KEY_ALBUM) ?: "").toString()
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val track = LocalTrack(
                title = title,
                artist = artist,
                album = album,
                duration = duration,
                playedBy = controller.packageName
        )
        getPlaybackController(controller).updateTrack(track)
    }

    fun onStateChanged(controller: MediaController, state: PlaybackState) {
        getPlaybackController(controller).updatePlayBackState(state)
    }
}
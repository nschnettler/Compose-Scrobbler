package de.schnettler.scrobble

import android.media.MediaMetadata
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.ServiceCoroutineScope
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import javax.inject.Inject

class PlayBackTracker @Inject constructor(
        private val repo: ScrobbleRepository,
        private val notificationManager: ScrobbleNotificationManager,
        private val scope: ServiceCoroutineScope,
        private val authProvider: LastFmAuthProvider
) {
    private val playerStates = hashMapOf<String, PlaybackController>()

    private fun getPlaybackController(packageName: String) =
            playerStates[packageName] ?: PlaybackController(repo, notificationManager, scope, authProvider).also {
                playerStates[packageName] = it }

    fun onMetadataChanged(packageName: String, metadata: MediaMetadata) {
        val title = (metadata.getText(MediaMetadata.METADATA_KEY_TITLE) ?: "").toString()
        val artist = ((metadata.getText(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata.getText(MediaMetadata
                .METADATA_KEY_ALBUM_ARTIST)) ?: "").toString()
        val album = (metadata.getText(MediaMetadata.METADATA_KEY_ALBUM) ?: "").toString()
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val track = LocalTrack(
                name = title,
                artist = artist,
                album = album,
                duration = duration,
                playedBy = packageName
        )
        getPlaybackController(packageName).updateTrack(track)
    }

    fun onStateChanged(packageName: String, state: PlaybackState) {
        getPlaybackController(packageName).updatePlayBackState(state)
    }
}
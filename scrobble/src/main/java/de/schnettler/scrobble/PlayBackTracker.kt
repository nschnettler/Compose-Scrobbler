package de.schnettler.scrobble

import android.media.MediaMetadata
import android.media.session.PlaybackState
import de.schnettler.database.models.LocalTrack
import javax.inject.Inject

class PlayBackTracker @Inject constructor(
    private val scrobbler: Scrobbler
) {
    private val playerStates = hashMapOf<String, PlaybackController>()

    private fun getPlaybackController(packageName: String) =
        playerStates[packageName] ?: PlaybackController(scrobbler).also {
            playerStates[packageName] = it
        }

    fun onMetadataChanged(packageName: String, metadata: MediaMetadata) {
        val title = (metadata.getText(MediaMetadata.METADATA_KEY_TITLE) ?: "").toString()
        val artist = ((metadata.getText(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata.getText(
            MediaMetadata
                .METADATA_KEY_ALBUM_ARTIST
        )) ?: "").toString()
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
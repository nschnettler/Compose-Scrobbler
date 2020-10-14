package de.schnettler.scrobble

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState

class MediaControllerCallback(
    private val controller: MediaController,
    private val playbackTracker: PlaybackTracker
) : MediaController.Callback() {

    override fun onPlaybackStateChanged(state: PlaybackState?) {
        state?.let {
            playbackTracker.onStateChanged(
                packageName = controller.packageName,
                state = it
            )
        }
    }

    override fun onMetadataChanged(metadata: MediaMetadata?) {
        metadata?.let {
            playbackTracker.onMetadataChanged(
                packageName = controller.packageName,
                metadata = it
            )
        }
    }
}
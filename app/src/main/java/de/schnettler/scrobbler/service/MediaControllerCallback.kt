package de.schnettler.scrobbler.service

import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState

class MediaControllerCallback(private val controller: MediaController, private val playbackTracker: PlayBackTracker):
        MediaController.Callback() {


    override fun onPlaybackStateChanged(state: PlaybackState?) {
        state?.let { playbackTracker.onStateChanged(controller = controller, state = it) }
    }

    override fun onMetadataChanged(metadata: MediaMetadata?) {
        metadata?.let { playbackTracker.onMetadataChanged(controller = controller, metadata = it) }
    }
}
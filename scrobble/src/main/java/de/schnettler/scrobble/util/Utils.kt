package de.schnettler.scrobble.util

import android.media.session.PlaybackState

fun PlaybackState.isPlaying() = state == PlaybackState.STATE_PLAYING
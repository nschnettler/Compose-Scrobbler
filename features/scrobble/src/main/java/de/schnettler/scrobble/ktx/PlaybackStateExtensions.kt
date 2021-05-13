package de.schnettler.scrobble.ktx

import android.media.session.PlaybackState

fun PlaybackState.isPlaying() = state == PlaybackState.STATE_PLAYING
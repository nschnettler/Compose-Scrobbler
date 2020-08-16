package de.schnettler.scrobble

import android.media.session.PlaybackState
import de.schnettler.repo.mapping.response.LastFmResponse
import timber.log.Timber

fun PlaybackState.isPlaying() = state == PlaybackState.STATE_PLAYING

fun <T> LastFmResponse<T>.printResult() = when (this) {
    is LastFmResponse.ERROR -> Timber.e("${this.error}")
    is LastFmResponse.SUCCESS<*> -> Timber.d("${this.data}")
}
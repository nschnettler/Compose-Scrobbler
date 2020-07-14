package de.schnettler.scrobble

import android.media.session.PlaybackState
import de.schnettler.repo.mapping.LastFmPostResponse
import timber.log.Timber

fun PlaybackState.isPlaying() = state == PlaybackState.STATE_PLAYING

fun <T>LastFmPostResponse<T>.printResult() = when(this) {
    is LastFmPostResponse.ERROR -> Timber.e("${this.error}")
    is LastFmPostResponse.SUCCESS<*> -> Timber.d("${this.data}")
}
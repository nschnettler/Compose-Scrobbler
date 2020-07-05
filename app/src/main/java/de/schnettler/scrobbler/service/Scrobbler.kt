package de.schnettler.scrobbler.service

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.repo.ServiceCoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class Scrobbler @Inject constructor(val dao: LocalTrackDao, val scope: ServiceCoroutineScope) {

    private fun saveTrack(track: LocalTrack) {
        scope.launch {
           dao.insertTrack(track)
        }
    }

    fun submitPlaybackItem(playbackItem: PlaybackItem): Boolean {
        Timber.d("[Submit] $playbackItem, ${playbackItem.playPercentage()} %")
        return if (playbackItem.track.canBeScrobbled() && playbackItem.playedEnough()) {
            saveTrack(playbackItem.map())
            Timber.d("[Save] $playbackItem, ${playbackItem.playPercentage()} %")
            true
        } else {
            false
        }
    }
}

fun PlaybackItem.map() = LocalTrack(
        title = track.title,
        artist = track.artist,
        album = track.album,
        duration = track.duration,

        startTime = timestamp,
        endTime = System.currentTimeMillis(),
        amountPlayed = amountPlayed,
        playedBy = playedBy
)
package de.schnettler.scrobble

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.ServiceCoroutineScope
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.scrobble.work.RESULT_COUNT
import de.schnettler.scrobble.work.RESULT_DESCRIPTION
import de.schnettler.scrobble.work.RESULT_TRACKS
import de.schnettler.scrobble.work.SUBMIT_CACHED_SCROBBLES_WORK
import de.schnettler.scrobble.work.ScrobbleWorker
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class Scrobbler @Inject constructor(
    private val workManager: WorkManager,
    private val notificationManager: ScrobbleNotificationManager,
    private val repo: ScrobbleRepository,
    private val scope: ServiceCoroutineScope,
    private val authProvider: LastFmAuthProvider
) {
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()

    init {
        workManager.getWorkInfosForUniqueWorkLiveData(SUBMIT_CACHED_SCROBBLES_WORK)
            .observeForever { workInfos ->
                workInfos.forEach { info ->
                    if (info.state == WorkInfo.State.SUCCEEDED) {
                        val tracks = info.outputData.getStringArray(RESULT_TRACKS)
                        tracks?.let {
                            notificationManager.scrobbledNotification(
                                it,
                                info.outputData.getInt(RESULT_COUNT, -1),
                                info.outputData.getString(RESULT_DESCRIPTION) ?: ""
                            )
                        }
                    }
                }
            }
    }

    fun submitScrobble(track: LocalTrack) {
        if (track.readyToScrobble()) {
            // 1. Cache Scrobble
            Timber.d("[Cache] $track")
            repo.saveTrack(track.copy(status = ScrobbleStatus.LOCAL))

            // 2. Schedule Workmanager Work
            scheduleScrobble()
        } else {
            Timber.d("[Skip] $track")
        }
    }

    private fun scheduleScrobble(): UUID {
        val request = OneTimeWorkRequestBuilder<ScrobbleWorker>()
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniqueWork(
            SUBMIT_CACHED_SCROBBLES_WORK,
            ExistingWorkPolicy.KEEP,
            request
        )
        return request.id
    }

    fun notifyNowPlaying(track: LocalTrack?) {
        updateNowPlayingNotification(track)
        Timber.d("[New] $track")
        track?.let {
            scope.launch {
                if (authProvider.loggedIn()) {
                    val result = repo.submitNowPlaying(track)
                    result.printResult()
                }
            }
        }
    }

    fun updateNowPlayingNotification(current: LocalTrack?) {
        if (current == null) {
            notificationManager.cancelNotifications(NOW_PLAYING_ID)
        } else {
            notificationManager.updateNowPlayingNotification(current)
        }
    }
}
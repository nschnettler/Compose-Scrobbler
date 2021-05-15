package de.schnettler.scrobble

import androidx.work.WorkInfo
import androidx.work.WorkManager
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.scrobble.notification.CACHE_ID
import de.schnettler.scrobble.notification.NOW_PLAYING_ID
import de.schnettler.scrobble.notification.ScrobbleNotificationManager
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.ScrobbleStatus
import de.schnettler.scrobbler.persistence.PreferenceRequestStore
import de.schnettler.scrobbler.submission.domain.RESULT_COUNT
import de.schnettler.scrobbler.submission.domain.RESULT_DESCRIPTION
import de.schnettler.scrobbler.submission.domain.RESULT_TRACKS
import de.schnettler.scrobbler.submission.domain.SUBMIT_CACHED_SCROBBLES_WORK
import de.schnettler.scrobbler.submission.domain.SubmissionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class Scrobbler @Inject constructor(
    workManager: WorkManager,
    private val notificationManager: ScrobbleNotificationManager,
    private val repo: SubmissionRepository,
    private val scope: CoroutineScope,
    private val dataStoreManager: DataStoreManager
) {
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

    suspend fun submitScrobble(track: Scrobble): Boolean {
        val scrobbleThreshold = dataStoreManager.getPreference(PreferenceRequestStore.scrobblePoint)
        return if (track.readyToScrobble(scrobbleThreshold)) {
            // 1. Cache Scrobble
            val toBeSaved = track.copy(status = ScrobbleStatus.LOCAL)
            if (repo.saveTrack(toBeSaved) == -1L) {
                Timber.d("[Cache] Error while saving ${track.name}")
                notificationManager.errorNotification("Couldn't cache ${track.name}")
                notificationManager.cancelNotifications(CACHE_ID)
            } else {
                notificationManager.cachedNotification(track)
                Timber.d("[Cache] Saved ${track.name}")
            }

            // 2. Schedule Workmanager Work
            if (dataStoreManager.getPreference(PreferenceRequestStore.autoScrobble)) {
                repo.scheduleScrobble()
            }
            true
        } else {
            Timber.d("[Cache] Skipped ${track.name}")
            false
        }
    }

    fun notifyNowPlaying(track: Scrobble?) = scope.launch {
        val nowPlayingSubmissionAllowed = dataStoreManager.getPreference(PreferenceRequestStore.submitNowPlaying)
        if (nowPlayingSubmissionAllowed && track != null) {
            when (val result = repo.submitNowPlaying(track)) {
                is LastFmResponse.SUCCESS -> {
                    notificationManager.updateNowPlayingNotification(track)
                    Timber.d("[NowPlaying] Submitted ${track.name}")
                }
                is LastFmResponse.ERROR -> {
                    val error = result.error?.title
                    notificationManager.errorNotification(error, R.string.np_error_title)
                    notificationManager.cancelNotifications(NOW_PLAYING_ID)
                    Timber.d("[NowPlaying] Submission Error: $error")
                }
                is LastFmResponse.EXCEPTION -> {
                    val message = result.exception.localizedMessage
                    notificationManager.errorNotification(message, R.string.np_error_title)
                    notificationManager.cancelNotifications(NOW_PLAYING_ID)
                    Timber.d("[NowPlaying] Submission Exception: $message")
                }
            }
        }
    }
}
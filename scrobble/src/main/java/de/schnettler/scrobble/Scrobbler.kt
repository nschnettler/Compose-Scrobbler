package de.schnettler.scrobble

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.mapping.response.LastFmResponse
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_KEY
import de.schnettler.repo.work.RESULT_COUNT
import de.schnettler.repo.work.RESULT_DESCRIPTION
import de.schnettler.repo.work.RESULT_TRACKS
import de.schnettler.repo.work.SUBMIT_CACHED_SCROBBLES_WORK
import de.schnettler.scrobble.notification.CACHE_ID
import de.schnettler.scrobble.notification.NOW_PLAYING_ID
import de.schnettler.scrobble.notification.ScrobbleNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class Scrobbler @Inject constructor(
    workManager: WorkManager,
    private val notificationManager: ScrobbleNotificationManager,
    private val repo: ScrobbleRepository,
    private val scope: CoroutineScope,
    private val prefs: FlowSharedPreferences
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

    fun submitScrobble(track: Scrobble) {
        val scrobbleThreshold = prefs.getFloat(SCROBBLE_POINT_KEY, SCROBBLE_POINT_DEFAULT).get()
        if (track.readyToScrobble(scrobbleThreshold)) {
            // 1. Cache Scrobble
            val toBeSaved = track.copy(status = ScrobbleStatus.LOCAL)
            scope.launch {
                if (repo.saveTrack(toBeSaved) == -1L) {
                    Timber.d("[Cache] Error while saving ${track.name}")
                    notificationManager.errorNotification("Couldn't cache ${track.name}")
                    notificationManager.cancelNotifications(CACHE_ID)
                } else {
                    notificationManager.cachedNotification(track)
                    Timber.d("[Cache] Saved ${track.name}")
                }
            }

            // 2. Schedule Workmanager Work
            if (prefs.getBoolean(AUTO_SCROBBLE_KEY, AUTO_SCROBBLE_DEFAULT).get()) { repo.scheduleScrobble() }
        } else {
            Timber.d("[Cache] Skipped ${track.name}")
        }
    }

    fun notifyNowPlaying(track: Scrobble?) {
        if (prefs.getBoolean(SUBMIT_NOWPLAYING_KEY, SUBMIT_NOWPLAYING_DEFAULT).get() && track != null) {
            scope.launch {
                when (val result = repo.submitNowPlaying(track)) {
                    is LastFmResponse.SUCCESS -> {
                        notificationManager.updateNowPlayingNotification(track)
                        Timber.d("[NowPlaying] Submitted ${track.name}")
                    }
                    is LastFmResponse.ERROR -> {
                        val error = result.error?.title
                        notificationManager.errorNotification("Couldn't submit NP: $error")
                        notificationManager.cancelNotifications(NOW_PLAYING_ID)
                        Timber.d("[NowPlaying] Submission Error: $error")
                    }
                }
            }
        }
    }
}
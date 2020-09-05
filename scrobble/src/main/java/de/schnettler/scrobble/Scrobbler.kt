package de.schnettler.scrobble

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.repo.ScrobbleRepository
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.di.ServiceCoroutineScope
import de.schnettler.repo.preferences.PreferenceConstants
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_KEY
import de.schnettler.repo.work.RESULT_COUNT
import de.schnettler.repo.work.RESULT_DESCRIPTION
import de.schnettler.repo.work.RESULT_TRACKS
import de.schnettler.repo.work.SUBMIT_CACHED_SCROBBLES_WORK
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
        Timber.d("[Cache] Caching Started")
        val scrobbleThreshold = prefs.getFloat(SCROBBLE_POINT_KEY, SCROBBLE_POINT_DEFAULT).get()
        if (track.readyToScrobble(scrobbleThreshold)) {
            // 1. Cache Scrobble
            val toBeSaved = track.copy(status = ScrobbleStatus.LOCAL)
            Timber.d("[Cache] Saving ${track.name}")
            scope.launch {
                val result = repo.saveTrack(toBeSaved)
                if (result == -1L) {
                    Timber.d("[Cache] Error while saving ${track.name}")
                    notificationManager.errorNotification("Couldn't cache ${track.name}")
                } else {
                    Timber.d("[Cache] Saved ${track.name}")
                }
            }

            // 2. Schedule Workmanager Work
            if (prefs.getBoolean(
                    PreferenceConstants.AUTO_SCROBBLE_KEY, PreferenceConstants.AUTO_SCROBBLE_DEFAULT
                ).get()) {
                repo.scheduleScrobble()
            }
        } else {
            Timber.d("[Cache] Skipped ${track.name}")
        }
    }

    fun notifyNowPlaying(track: Scrobble?) {
        Timber.d("[NowPlaying] NP submission started")
        updateNowPlayingNotification(track)
        if (prefs.getBoolean(SUBMIT_NOWPLAYING_KEY, SUBMIT_NOWPLAYING_DEFAULT).get() && track != null) {
            scope.launch {
                if (authProvider.loggedIn()) {
                    Timber.d("[NowPlaying] Submitting ${track.name}")
                    val result = repo.submitNowPlaying(track)
                    result.printResult()
                } else {
                    notificationManager.errorNotification("Couldn't submit NP: Unauthenticated")
                    Timber.d("[NowPlaying] Error: Unauthenticated")
                }
            }
        }
    }

    fun updateNowPlayingNotification(current: Scrobble?) {
        if (current == null) {
            notificationManager.cancelNotifications(NOW_PLAYING_ID)
        } else {
            notificationManager.updateNowPlayingNotification(current)
        }
    }
}
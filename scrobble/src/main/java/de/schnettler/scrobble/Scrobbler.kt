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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class Scrobbler @Inject constructor(
    workManager: WorkManager,
    private val notificationManager: ScrobbleNotificationManager,
    private val repo: ScrobbleRepository,
    private val scope: ServiceCoroutineScope,
    private val authProvider: LastFmAuthProvider,
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
            Timber.d("[Cache] $track")
            repo.saveTrack(track.copy(status = ScrobbleStatus.LOCAL))

            // 2. Schedule Workmanager Work
            if (prefs.getBoolean(
                    PreferenceConstants.AUTO_SCROBBLE_KEY, PreferenceConstants.AUTO_SCROBBLE_DEFAULT
                ).get()) {
                repo.scheduleScrobble()
            }
        } else {
            Timber.d("[Skip] $track")
        }
    }

    fun notifyNowPlaying(track: Scrobble?) {
        updateNowPlayingNotification(track)
        Timber.d("[New] $track")
        if (prefs.getBoolean(SUBMIT_NOWPLAYING_KEY, SUBMIT_NOWPLAYING_DEFAULT).get() && track != null) {
            scope.launch {
                if (authProvider.loggedIn()) {
                    val result = repo.submitNowPlaying(track)
                    result.printResult()
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
package de.schnettler.repo

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.Scrobble
import de.schnettler.lastfm.api.lastfm.PostService
import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.models.MutlipleScrobblesResponse
import de.schnettler.lastfm.models.ScrobbleResponse
import de.schnettler.lastfm.models.SingleScrobbleResponse
import de.schnettler.repo.mapping.response.LastFmResponse
import de.schnettler.repo.mapping.response.map
import de.schnettler.repo.model.SubmissionResult
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.repo.util.safePost
import de.schnettler.repo.work.MAX_SCROBBLE_BATCH_SIZE
import de.schnettler.repo.work.SUBMIT_CACHED_SCROBBLES_WORK
import de.schnettler.repo.work.ScrobbleWorker
import timber.log.Timber
import javax.inject.Inject

class ScrobbleRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val service: PostService,
    private val workManager: WorkManager,
    private val prefs: FlowSharedPreferences,
) {
    suspend fun saveTrack(track: Scrobble) = localTrackDao.forceInsert(track)

    suspend fun submitCachedScrobbles(): SubmissionResult {
        Timber.d("[Scrobble] Started cached scrobble submission")
        val cachedTracks = localTrackDao.getCachedTracks()
        Timber.d("[Scrobble] Found ${cachedTracks.size} cached scrobbles")

        val acceptedResult = mutableListOf<ScrobbleResponse>()
        val ignoredResult = mutableListOf<ScrobbleResponse>()
        val errors = mutableListOf<Errors>()
        val exceptions = mutableListOf<Throwable>()

        cachedTracks.chunked(MAX_SCROBBLE_BATCH_SIZE).map { input ->
            when (val submissionResponse = submitScrobbles(input)) {
                is LastFmResponse.SUCCESS -> {
                    submissionResponse.data?.also { response ->
                        val (accepted, ignored) = response.scrobble.partition { it.ignoredMessage.code == 0L }
                        localTrackDao.updateScrobbleStatus(accepted.map { it.timestamp })
                        acceptedResult.addAll(accepted)
                        ignoredResult.addAll(ignored)
                    }
                }
                is LastFmResponse.ERROR -> submissionResponse.error?.let { errors.add(it) }
                is LastFmResponse.EXCEPTION -> exceptions.add(submissionResponse.exception)
            }
        }
        return SubmissionResult(acceptedResult, ignoredResult, errors, exceptions)
    }

    suspend fun submitScrobble(track: Scrobble): LastFmResponse<SingleScrobbleResponse> {
        return safePost {
            service.submitScrobble(
                artist = track.artist,
                track = track.name,
                timestamp = track.timeStampString(),
                album = track.album,
                duration = track.durationUnix(),
            ).map()
        }
    }

    suspend fun submitNowPlaying(track: Scrobble): LastFmResponse<ScrobbleResponse> {
        return safePost {
            service.submitNowPlaying(
                artist = track.artist,
                track = track.name,
                album = track.album,
                duration = track.durationUnix(),
            ).map()
        }
    }

    private suspend fun submitScrobbles(tracks: List<Scrobble>): LastFmResponse<MutlipleScrobblesResponse> {
        val parameters = tracks.mapIndexed { index: Int, scrobble: Scrobble ->
            mapOf(
                "artist[$index]" to scrobble.artist,
                "track[$index]" to scrobble.name,
                "album[$index]" to scrobble.album,
                "duration[$index]" to scrobble.durationUnix(),
                "timestamp[$index]" to scrobble.timeStampString(),
            )
        }.reduce { acc, map -> acc + map }

        return safePost { service.submitMultipleScrobbles(parameters).map() }
    }

    suspend fun markScrobblesAsSubmitted(vararg tracks: Scrobble) {
        localTrackDao.updateScrobbleStatus(tracks.map { it.timestamp })
    }

    suspend fun deleteScrobble(scrobble: Scrobble) = localTrackDao.delete(scrobble)

    fun scheduleScrobble() {
        val constraints = Constraints.Builder().apply {
            val constraintSet =
                prefs.getStringSet(SCROBBLE_CONSTRAINTS_KEY, SCROBBLE_CONSTRAINTS_DEFAULT).get()
            if (constraintSet.contains(SCROBBLE_CONSTRAINTS_BATTERY)) setRequiresBatteryNotLow(true)
            if (constraintSet.contains(SCROBBLE_CONSTRAINTS_NETWORK)) setRequiredNetworkType(NetworkType.UNMETERED)
        }.build()
        val request = OneTimeWorkRequestBuilder<ScrobbleWorker>().setConstraints(constraints).build()
        workManager.enqueueUniqueWork(
            SUBMIT_CACHED_SCROBBLES_WORK,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    suspend fun submitScrobbleEdit(scrobble: Scrobble) {
        localTrackDao.updateTrackData(scrobble.timestamp, scrobble.name, scrobble.artist, scrobble.album)
    }
}

private fun listToMap(list: List<String>, key: String) =
    list.withIndex().associateBy({ "$key[${it.index}]" }, { it.value })
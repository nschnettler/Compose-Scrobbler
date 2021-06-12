package de.schnettler.scrobbler.submission.domain

import androidx.work.Constraints.Builder
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.lastfm.map.ResponseToLastFmResponseMapper
import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.model.ScrobbleStatus
import de.schnettler.scrobbler.persistence.PreferenceRequestStore
import de.schnettler.scrobbler.persistence.PreferenceRequestStore.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.scrobbler.persistence.PreferenceRequestStore.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.scrobbler.submission.api.SubmissionApi
import de.schnettler.scrobbler.submission.db.SubmissionFailureDao
import de.schnettler.scrobbler.submission.map.MultiSubmissionResponseMapper
import de.schnettler.scrobbler.submission.map.ScrobbleResponseToSubmissionFailureEntityMapper
import de.schnettler.scrobbler.submission.map.SingleSubmissionResponseMapper
import de.schnettler.scrobbler.submission.model.MultiScrobbleResponse
import de.schnettler.scrobbler.submission.model.NowPlayingResponse
import de.schnettler.scrobbler.submission.model.SingleScrobbleResponse
import de.schnettler.scrobbler.submission.model.SubmissionResult
import de.schnettler.scrobbler.submission.model.SubmissionResults
import de.schnettler.scrobbler.submission.safePost
import timber.log.Timber
import javax.inject.Inject

class SubmissionRepository @Inject constructor(
    private val submissionDao: SubmissionDao,
    private val submissionApi: SubmissionApi,
    private val workManager: WorkManager,
    private val dataStoreManager: DataStoreManager,
    private val failureMapper: ScrobbleResponseToSubmissionFailureEntityMapper,
    private val submissionFailureDao: SubmissionFailureDao,
    private val multiSubmissionResponseMapper: MultiSubmissionResponseMapper,
    private val singleSubmissionResponseMapper: SingleSubmissionResponseMapper,
) {
    suspend fun saveTrack(track: Scrobble) = submissionDao.forceInsert(track)

    suspend fun submitCachedScrobbles(): SubmissionResults {
        Timber.d("[Scrobble] Started cached scrobble submission")
        val cachedTracks = submissionDao.getCachedTracks()
        Timber.d("[Scrobble] Found ${cachedTracks.size} cached scrobbles")

        val results = if (cachedTracks.size == 1) {
            // 1. Submit single scrobble
            val response = submitScrobble(cachedTracks.first())
            val result = singleSubmissionResponseMapper.map(response)
            handleSubmissionResult(result)
            listOf(result)
        } else {
            // 2. Submit multiple scrobbles
            cachedTracks.chunked(MAX_SCROBBLE_BATCH_SIZE).map { scrobbles ->
                val response = submitScrobbles(scrobbles)
                val result = multiSubmissionResponseMapper.map(response)
                handleSubmissionResult(result)
                return@map result
            }
        }

        val successes = results.filterIsInstance<SubmissionResult.Success>()
        return SubmissionResults(
            accepted = successes.flatMap { it.accepted },
            ignored = successes.flatMap { it.ignored },
            errors = results.filterIsInstance<SubmissionResult.Error>(),
        )
    }

    private suspend fun handleSubmissionResult(submissionResult: SubmissionResult) {
        if (submissionResult is SubmissionResult.Success) {
            // 1. Handle accepted Scrobbles
            submissionResult.accepted.let { accepted ->
                val acceptedTimestamps = accepted.map { it.timestamp }

                // Mark scrobble as submitted
                submissionDao.updateScrobbleStatus(acceptedTimestamps, ScrobbleStatus.SCROBBLED)

                // Remove scrobble from failed_scrobbles table
                submissionFailureDao.deleteEntries(acceptedTimestamps)
            }

            // 2. Handle ignored Scrobbles
            submissionResult.ignored.let { ignored ->
                // Update ScrobbleStatus
                submissionDao.updateScrobbleStatus(ignored.map { it.timestamp }, ScrobbleStatus.SUBMISSION_FAILED)

                // Add to failureDb
                val mapped = failureMapper.forLists().invoke(ignored)
                submissionFailureDao.insertAll(mapped)
            }
        }
    }

    suspend fun submitScrobble(track: Scrobble): LastFmResponse<SingleScrobbleResponse> {
        return safePost {
            ResponseToLastFmResponseMapper.map(
                submissionApi.submitScrobble(
                    artist = track.artist,
                    track = track.name,
                    timestamp = track.timeStampString(),
                    album = track.album,
                    duration = track.durationUnix(),
                )
            )
        }
    }

    suspend fun submitNowPlaying(track: Scrobble): LastFmResponse<NowPlayingResponse> {
        return safePost {
            ResponseToLastFmResponseMapper.map(
                submissionApi.submitNowPlaying(
                    artist = track.artist,
                    track = track.name,
                    album = track.album,
                    duration = track.durationUnix(),
                )
            )
        }
    }

    private suspend fun submitScrobbles(tracks: List<Scrobble>): LastFmResponse<MultiScrobbleResponse> {
        val parameters = tracks.mapIndexed { index: Int, scrobble: Scrobble ->
            mapOf(
                "artist[$index]" to scrobble.artist,
                "track[$index]" to scrobble.name,
                "album[$index]" to scrobble.album,
                "duration[$index]" to scrobble.durationUnix(),
                "timestamp[$index]" to scrobble.timeStampString(),
            )
        }.reduce { acc, map -> acc + map }

        return safePost {
            ResponseToLastFmResponseMapper.map(
                submissionApi.submitMultipleScrobbles(
                    parameters
                )
            )
        }
    }

    suspend fun markScrobblesAsSubmitted(vararg tracks: Scrobble) {
        submissionDao.updateScrobbleStatus(tracks.map { it.timestamp }, ScrobbleStatus.SCROBBLED)
    }

    suspend fun deleteScrobble(scrobble: Scrobble) = submissionDao.delete(scrobble)

    suspend fun scheduleScrobble() {
        val constraints = Builder().apply {
            val constraintSet = dataStoreManager.getPreference(PreferenceRequestStore.scrobbleConstraints)
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
        submissionDao.updateTrackData(scrobble.timestamp, scrobble.name, scrobble.artist, scrobble.album)
    }
}
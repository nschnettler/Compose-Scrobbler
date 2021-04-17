package de.schnettler.scrobbler.submission.domain

import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.lastfm.map.ResponseToLastFmResponseMapper
import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.models.LastFmResponse
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.persistence.PreferenceConstants.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.scrobbler.persistence.PreferenceConstants.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.scrobbler.persistence.PreferenceEntry
import de.schnettler.scrobbler.submission.api.SubmissionApi
import de.schnettler.scrobbler.submission.model.MutlipleScrobblesResponse
import de.schnettler.scrobbler.submission.model.NowPlayingResponse
import de.schnettler.scrobbler.submission.model.ScrobbleResponse
import de.schnettler.scrobbler.submission.model.SingleScrobbleResponse
import de.schnettler.scrobbler.submission.model.SubmissionResult
import de.schnettler.scrobbler.submission.safePost
import timber.log.Timber
import javax.inject.Inject

class SubmissionRepository @Inject constructor(
    private val submissionDao: SubmissionDao,
    private val submissionApi: SubmissionApi,
    private val workManager: androidx.work.WorkManager,
    private val dataStoreManager: DataStoreManager,
) {
    suspend fun saveTrack(track: Scrobble) = submissionDao.forceInsert(track)

    suspend fun submitCachedScrobbles(): SubmissionResult {
        Timber.d("[Scrobble] Started cached scrobble submission")
        val cachedTracks = submissionDao.getCachedTracks()
        Timber.d("[Scrobble] Found ${cachedTracks.size} cached scrobbles")

        val acceptedResult = mutableListOf<ScrobbleResponse>()
        val ignoredResult = mutableListOf<ScrobbleResponse>()
        val errors = mutableListOf<Errors>()
        val exceptions = mutableListOf<Throwable>()

        if (cachedTracks.size == 1) {
            when (val submissionResponse = submitScrobble(cachedTracks.first())) {
                is LastFmResponse.SUCCESS -> {
                    submissionResponse.data?.also { response ->
                        val scrobbleResponse = response.scrobble
                        submissionDao.updateScrobbleStatus(listOf(response.scrobble.timestamp))
                        if (scrobbleResponse.ignoredMessage.code == 0L) {
                            acceptedResult.add(scrobbleResponse)
                        } else {
                            ignoredResult.add(scrobbleResponse)
                        }
                    }
                }
                is LastFmResponse.ERROR -> submissionResponse.error?.let { errors.add(it) }
                is LastFmResponse.EXCEPTION -> exceptions.add(submissionResponse.exception)
            }
        } else {
            cachedTracks.chunked(MAX_SCROBBLE_BATCH_SIZE).map { input ->
                when (val submissionResponse = submitScrobbles(input)) {
                    is LastFmResponse.SUCCESS -> {
                        submissionResponse.data?.also { response ->
                            val (accepted, ignored) = response.scrobble.partition { it.ignoredMessage.code == 0L }
                            submissionDao.updateScrobbleStatus(accepted.map { it.timestamp })
                            acceptedResult.addAll(accepted)
                            ignoredResult.addAll(ignored)
                        }
                    }
                    is LastFmResponse.ERROR -> submissionResponse.error?.let { errors.add(it) }
                    is LastFmResponse.EXCEPTION -> exceptions.add(submissionResponse.exception)
                }
            }
        }
        return SubmissionResult(acceptedResult, ignoredResult, errors, exceptions)
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

        return safePost {
            ResponseToLastFmResponseMapper.map(
                submissionApi.submitMultipleScrobbles(
                    parameters
                )
            )
        }
    }

    suspend fun markScrobblesAsSubmitted(vararg tracks: Scrobble) {
        submissionDao.updateScrobbleStatus(tracks.map { it.timestamp })
    }

    suspend fun deleteScrobble(scrobble: Scrobble) = submissionDao.delete(scrobble)

    suspend fun scheduleScrobble() {
        val constraints = androidx.work.Constraints.Builder().apply {
            val constraintSet = dataStoreManager.getPreference(PreferenceEntry.ScrobbleConstraints)
            if (constraintSet.contains(SCROBBLE_CONSTRAINTS_BATTERY)) setRequiresBatteryNotLow(true)
            if (constraintSet.contains(SCROBBLE_CONSTRAINTS_NETWORK)) setRequiredNetworkType(androidx.work.NetworkType.UNMETERED)
        }.build()
        val request = androidx.work.OneTimeWorkRequestBuilder<ScrobbleWorker>().setConstraints(constraints).build()
        workManager.enqueueUniqueWork(
            SUBMIT_CACHED_SCROBBLES_WORK,
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
    }

    suspend fun submitScrobbleEdit(scrobble: Scrobble) {
        submissionDao.updateTrackData(scrobble.timestamp, scrobble.name, scrobble.artist, scrobble.album)
    }
}
package de.schnettler.repo.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import de.schnettler.lastfm.models.Errors
import de.schnettler.lastfm.models.ScrobbleResponse
import de.schnettler.repo.ScrobbleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

const val SUBMIT_CACHED_SCROBBLES_WORK = "submit_cached_scrobbles"
const val MAX_SCROBBLE_BATCH_SIZE = 50
const val RESULT_COUNT = "count"
const val RESULT_DESCRIPTION = "description"
const val RESULT_TRACKS = "tracks"

class ScrobbleWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repo: ScrobbleRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val submissionResult = repo.submitCachedScrobbles()

        return@withContext when {
            submissionResult.accepted.isNotEmpty() -> Result.success(generateSuccessData(submissionResult.accepted))
            submissionResult.errors.contains(Errors.OFFLINE)
                    || submissionResult.errors.contains(Errors.UNAVAILABLE) -> Result.retry()
            else -> Result.failure()
        }
    }

    private fun generateSuccessData(acceptedTracks: List<ScrobbleResponse>): Data {
        // Result has to be Success here
        val max = min(5, acceptedTracks.size)
        val scrobbles = acceptedTracks
            .subList(0, max).map { "${it.artist.correctValue} ⦁ ${it.track.correctValue}" }
            .toTypedArray()
        return Data.Builder()
            .putStringArray(RESULT_TRACKS, scrobbles)
            .putInt(RESULT_COUNT, acceptedTracks.size)
            .putString(RESULT_DESCRIPTION, acceptedTracks.first().track.correctValue)
            .build()
    }
}
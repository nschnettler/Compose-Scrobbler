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
import de.schnettler.lastfm.models.MutlipleScrobblesResponse
import de.schnettler.lastfm.models.ScrobbleResponse
import de.schnettler.lastfm.models.SingleScrobbleResponse
import de.schnettler.repo.mapping.response.LastFmResponse
import de.schnettler.repo.mapping.response.map
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.repo.work.SUBMIT_CACHED_SCROBBLES_WORK
import de.schnettler.repo.work.ScrobbleWorker
import javax.inject.Inject

class ScrobbleRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val service: PostService,
    private val workManager: WorkManager,
    private val prefs: FlowSharedPreferences
) {
    suspend fun saveTrack(track: Scrobble) = localTrackDao.forceInsert(track)

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

    suspend fun getCachedTracks() = localTrackDao.getCachedTracks()

    suspend fun submitScrobbles(tracks: List<Scrobble>): LastFmResponse<MutlipleScrobblesResponse> {
        val artists = tracks.map { it.artist }
        val albums = tracks.map { it.album }
        val names = tracks.map { it.name }
        val durations = tracks.map { it.durationUnix() }
        val timestamps = tracks.map { it.timeStampString() }

        return safePost { service.submitMultipleScrobbles(
            track = names.mapIndexed { index, name -> "track[$index]=$name" }.joinToString("&"),
            artist = artists.mapIndexed { index, name -> "artist[$index]=$name" }.joinToString("&"),
            album = albums.mapIndexed { index, name -> "album[$index]=$name" }.joinToString("&"),
            duration = durations.mapIndexed { index, name -> "duration[$index]=$name" }.joinToString("&"),
            timestamp = timestamps.mapIndexed { index, name -> "timestamp[$index]=$name" }.joinToString("&"),
        ).map() }
    }

    suspend fun markScrobblesAsSubmitted(tracks: List<Scrobble>) {
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

@Suppress("TooGenericExceptionCaught")
inline fun <T> safePost(post: () -> LastFmResponse<T>): LastFmResponse<T> =
    try {
        post()
    } catch (ex: Exception) {
        LastFmResponse.EXCEPTION(ex)
    }
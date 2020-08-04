package de.schnettler.repo

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.lastfm.ScrobblerService
import de.schnettler.lastfm.models.MutlipleScrobblesResponse
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.di.ServiceCoroutineScope
import de.schnettler.repo.mapping.LastFmResponse
import de.schnettler.repo.mapping.map
import de.schnettler.repo.util.createBody
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScrobbleRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val scope: ServiceCoroutineScope,
    private val service: ScrobblerService,
    private val authProvider: LastFmAuthProvider
) {
    fun saveTrack(track: LocalTrack) {
        scope.launch {
            localTrackDao.insertOrUpdatTrack(track)
        }
    }

    suspend fun createAndSubmitScrobble(track: LocalTrack) = service.submitScrobble(
        method = LastFmService.METHOD_SCROBBLE,
        artist = track.artist,
        track = track.name,
        timestamp = track.timeStampString(),
        album = track.album,
        duration = track.durationUnix(),
        sessionKey = authProvider.getSessionKeyOrThrow(),
        signature = createSignature(
            mutableMapOf(
                "method" to LastFmService.METHOD_SCROBBLE,
                "artist" to track.artist,
                "track" to track.name,
                "album" to track.album,
                "duration" to track.durationUnix(),
                "timestamp" to track.timeStampString(),
                "sk" to authProvider.getSessionKeyOrThrow()
            )
        )
    ).map()

    suspend fun submitNowPlaying(track: LocalTrack) = service.submitNowPlaying(
        method = LastFmService.METHOD_NOWPLAYING,
        artist = track.artist,
        track = track.name,
        album = track.album,
        duration = track.durationUnix(),
        sessionKey = authProvider.getSessionKeyOrThrow(),
        signature = createSignature(
            mutableMapOf(
                "method" to LastFmService.METHOD_NOWPLAYING,
                "artist" to track.artist,
                "track" to track.name,
                "album" to track.album,
                "duration" to track.durationUnix(),
                "sk" to authProvider.getSessionKeyOrThrow()
            )
        )
    ).map()

    suspend fun getCachedTracks() = localTrackDao.getCachedTracks()

    suspend fun submitScrobbles(tracks: List<LocalTrack>): LastFmResponse<MutlipleScrobblesResponse> {
        val result: MutableMap<String, String> = mutableMapOf(
            "method" to LastFmService.METHOD_SCROBBLE,
            "sk" to authProvider.getSessionKeyOrThrow()
        )
        val artists = tracks.map { it.artist }
        val albums = tracks.map { it.album }
        val names = tracks.map { it.name }
        val durations = tracks.map { it.durationUnix() }
        val timestamps = tracks.map { it.timeStampString() }

        result.putAll(listToMap(artists, "artist"))
        result.putAll(listToMap(albums, "album"))
        result.putAll(listToMap(names, "track"))
        result.putAll(listToMap(timestamps, "timestamp"))
        result.putAll(listToMap(durations, "duration"))
        val signature = createSignature(result)

        result["api_sig"] = signature
        result["api_key"] = LastFmService.API_KEY
        result["format"] = "json"

        return service.submitMultipleScrobbles(createBody(result)).map()
    }
}

fun listToMap(list: List<String>, key: String) =
    list.withIndex().associateBy({ "$key[${it.index}]" }, { it.value })
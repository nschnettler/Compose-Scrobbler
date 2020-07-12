package de.schnettler.repo

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.lastfm.ScrobblerService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.map
import de.schnettler.repo.util.createSignature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScrobbleRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val scope: ServiceCoroutineScope,
    private val service: ScrobblerService,
    private val authProvider: LastFmAuthProvider
) {
    var currentTrack: LocalTrack? = null

    init {
        scope.launch {
            localTrackDao.getCurrentTrack().collect {
                currentTrack = it
            }
        }
    }

    fun saveTrack(track: LocalTrack) {
        scope.launch {
            localTrackDao.insertOrUpdatTrack(track)
        }
    }

    fun removeTrack(track: LocalTrack) {
        scope.launch {
            localTrackDao.delete(track)
        }
    }

    fun updateTrackAlbum(track: LocalTrack?, album: String) {
        scope.launch {
            track?.let { localTrackDao.updateAlbum(album, it.timestamp, it.playedBy) }

        }
    }

    suspend fun submitNowPlaying(track: LocalTrack) = service.submitNowPlaying(
            method = LastFmService.METHOD_NOWPLAYING,
            artist = track.artist,
            track = track.name,
            album = track.album,
            duration = track.durationUnix(),
            sessionKey = authProvider.getSessionKeyOrThrow(),
            signature = createSignature(
                    LastFmService.METHOD_NOWPLAYING,
                    mutableMapOf(
                            "artist" to track.artist,
                            "track" to track.name,
                            "album" to track.album,
                            "duration" to track.durationUnix(),
                            "sk" to authProvider.getSessionKeyOrThrow()
                    ),
                    LastFmService.SECRET
            )
    ).map()

    suspend fun createAndSubmitScrobble(track: LocalTrack)= service.submitScrobble(
            method = LastFmService.METHOD_SCROBBLE,
            artist = track.artist,
            track = track.name,
            timestamp = track.timeStampUnix(),
            album = track.album,
            duration = track.durationUnix(),
            sessionKey = authProvider.getSessionKeyOrThrow(),
            signature = createSignature(
                    LastFmService.METHOD_SCROBBLE,
                    mutableMapOf(
                            "artist" to track.artist,
                            "track" to track.name,
                            "album" to track.album,
                            "duration" to track.durationUnix(),
                            "timestamp" to track.timeStampUnix(),
                            "sk" to authProvider.getSessionKeyOrThrow()
                    ),
                    LastFmService.SECRET
            )
    ).map()
}
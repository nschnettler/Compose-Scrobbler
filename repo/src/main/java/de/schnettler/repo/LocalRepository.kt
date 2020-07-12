package de.schnettler.repo

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.lastfm.api.lastfm.LastFmService.Companion.METHOD_NOWPLAYING
import de.schnettler.lastfm.api.lastfm.LastFmService.Companion.METHOD_SCROBBLE
import de.schnettler.lastfm.api.lastfm.LastFmService.Companion.SECRET
import de.schnettler.lastfm.api.lastfm.ScrobblerService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.map
import de.schnettler.repo.util.createSignature
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val service: ScrobblerService,
    private val authProvider: LastFmAuthProvider
) {
    fun getData() = localTrackDao.getLocalTracks()

    suspend fun createAndSubmitScrobble(track: LocalTrack)= service.submitScrobble(
            method = METHOD_SCROBBLE,
            artist = track.artist,
            track = track.name,
            timestamp = track.timeStampUnix(),
            album = track.album,
            duration = track.durationUnix(),
            sessionKey = authProvider.getSessionKeyOrThrow(),
            signature = createSignature(
                    METHOD_SCROBBLE,
                    mutableMapOf(
                            "artist" to track.artist,
                            "track" to track.name,
                            "album" to track.album,
                            "duration" to track.durationUnix(),
                            "timestamp" to track.timeStampUnix(),
                            "sk" to authProvider.getSessionKeyOrThrow()
                    ),
                    SECRET
            )
    ).map()

    suspend fun submitNowPlaying(track: LocalTrack) = service.submitNowPlaying(
            method = METHOD_NOWPLAYING,
            artist = track.artist,
            track = track.name,
            album = track.album,
            duration = track.durationUnix(),
            sessionKey = authProvider.getSessionKeyOrThrow(),
            signature = createSignature(
                    METHOD_NOWPLAYING,
                    mutableMapOf(
                            "artist" to track.artist,
                            "track" to track.name,
                            "album" to track.album,
                            "duration" to track.durationUnix(),
                            "sk" to authProvider.getSessionKeyOrThrow()
                    ),
                    SECRET
            )
    ).map()
}
package de.schnettler.repo

import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.api.lastfm.ScrobbleRequest
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.util.createSignature
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val service: LastFmService,
    private val authProvider: LastFmAuthProvider
) {
    fun getData() = localTrackDao.getLocalTracks()

    fun requestScrobble(track: LocalTrack) {
        service.requestScrobble(track.asScrobble(authProvider.getSessionKeyOrThrow()))
    }

    fun LocalTrack.asScrobble(key: String) = ScrobbleRequest(
        artist = artist,
        track = name,
        timestamp = timestamp,
        album = album,
        duration = duration,
        api_sig = createSignature(LastFmService.METHOD_SCROBBLE, mutableMapOf(), LastFmService.SECRET),
        sk = key
    )
}
package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.fresh
import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.LocalTrack
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.mapToLocal
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val service: LastFmService,
    private val authProvider: LastFmAuthProvider
) {
    private val recentTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { _: String ->
            service.getUserRecentTrack(authProvider.getSessionKeyOrThrow()).map { it.mapToLocal() }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = {
                localTrackDao.getLocalTracks().combine(
                    localTrackDao.getNowPlaying()
                ) { tracks, nowPlaying ->
                    return@combine if (nowPlaying == null) {
                        tracks
                    } else {
                        listOf(nowPlaying) + tracks
                    }
                }
            },
            writer = { _: String, value: List<LocalTrack> ->
                localTrackDao.insertAll(value)
                val nowPlaying = value.firstOrNull() { it.status == ScrobbleStatus.PLAYING }
                if (nowPlaying != null) {
                    localTrackDao.forceInsert(nowPlaying)
                } else {
                    localTrackDao.deleteByStatus(ScrobbleStatus.PLAYING)
                }
            }
        )
    ).build()

    fun getRecentTracks() = recentTracksStore.stream(StoreRequest.cached("", true))
    suspend fun refreshRecentTracks() = recentTracksStore.fresh("")
}
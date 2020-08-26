package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.lastfm.models.RecentTracksDto
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.TrackMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.mapping.track.mapToLocal
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val service: LastFmService,
    private val authProvider: LastFmAuthProvider,
    private val trackDao: TrackDao,
) {
    val recentTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { _: String ->
            service.getUserRecentTrack(authProvider.getSessionKeyOrThrow())
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
            writer = { _: String, value: List<RecentTracksDto> ->
                // Insert as normal tracks
                val tracks = TrackMapper.forLists()(
                    value, List(value.size) { null })
                trackDao.insertAll(
                    tracks
                )

                // Convert to Local Track
                val local = value.map { it.mapToLocal() }
                val changedRows = localTrackDao.insertAll(local)
                // Update Metadata of local scrobbles
                local.filterIndexed { index, _ ->
                    changedRows[index] == -1L
                }.forEach { localTrackDao.updateTrackData(it.timestamp, it.name, it.artist, it.album) }

                // Update Now Playing
                val nowPlaying = local.firstOrNull { it.status == ScrobbleStatus.PLAYING }
                if (nowPlaying != null) {
                    localTrackDao.forceInsert(nowPlaying)
                } else {
                    localTrackDao.deleteByStatus(ScrobbleStatus.PLAYING)
                }
            }
        )
    ).build()

    fun getNumberOfCachedScrobbles() = localTrackDao.getNumberOfCachedScrobbles()
}
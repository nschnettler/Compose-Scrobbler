package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.ScrobbleStatus
import de.schnettler.lastfm.api.lastfm.UserService
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.mapping.track.ScrobbleMapper
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val localTrackDao: LocalTrackDao,
    private val userService: UserService
) {
    val recentTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { ScrobbleMapper.forLists()(userService.getUserRecentTrack()) },
        sourceOfTruth = SourceOfTruth.of(
            reader = { localTrackDao.getListeningHistory() },
            writer = { _: String, scrobbles: List<Scrobble> ->
                // Update local scrobbles with remote data
                val changedRows = localTrackDao.insertAll(scrobbles)
                scrobbles.filterIndexed { index, _ ->
                    changedRows[index] == -1L
                }.forEach { localTrackDao.updateTrackData(it.timestamp, it.name, it.artist, it.album) }

                // Update nowPlaying a) Replace b) Remove
                scrobbles.firstOrNull { it.status == ScrobbleStatus.PLAYING }?.let { nowPlaying ->
                    localTrackDao.forceInsert(nowPlaying)
                } ?: localTrackDao.deleteByStatus(ScrobbleStatus.PLAYING)
            }
        )
    ).build()

    fun getNumberOfCachedScrobbles() = localTrackDao.getNumberOfCachedScrobbles()
}
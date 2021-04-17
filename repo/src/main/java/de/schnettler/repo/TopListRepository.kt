package de.schnettler.repo

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.common.TimePeriod
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.daos.UserDao
import de.schnettler.scrobbler.core.model.TopListAlbum
import de.schnettler.scrobbler.core.model.TopListArtist
import de.schnettler.scrobbler.core.model.TopListTrack
import de.schnettler.lastfm.api.lastfm.UserService
import de.schnettler.repo.authentication.provider.LastFmAuthProviderImpl
import de.schnettler.repo.mapping.album.TopUserAlbumMapper
import de.schnettler.repo.mapping.artist.TopUserArtistMapper
import de.schnettler.repo.mapping.track.TopUserTrackMapper
import de.schnettler.repo.work.SpotifyWorker
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.core.model.ListType
import javax.inject.Inject

class TopListRepository @Inject constructor(
    private val userDao: UserDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val chartDao: ChartDao,
    private val userService: UserService,
    private val authProvider: LastFmAuthProviderImpl,
    private val workManager: WorkManager,
) {
    val topArtistStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            val session = authProvider.getSession()
            val response = userService.getTopArtists(timePeriod)
            if (timePeriod == TimePeriod.OVERALL) {
                userDao.updateArtistCount(session?.name.orEmpty(), response.info.total)
            }
            TopUserArtistMapper.forLists()(response.artist)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopArtists(listType = ListType.USER) },
            writer = { _: Any, entries: List<TopListArtist> ->
                artistDao.insertAll(entries.map { it.value })
                chartDao.forceInsertAll(entries.map { it.listing })
                startSpotifyImageWorker()
            }
        )
    ).build()

    val topAlbumStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            TopUserAlbumMapper.forLists()(
                userService.getTopAlbums(timePeriod)
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopAlbums() },
            writer = { _: Any, entries: List<TopListAlbum> ->
                albumDao.forceInsertAll(entries.map { it.value })
                chartDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()

    val topTracksStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            TopUserTrackMapper.forLists()(
                userService.getTopTracks(timePeriod)
            )
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopTracks() },
            writer = { _: Any, entries: List<TopListTrack> ->
                trackDao.insertAll(entries.map { it.value })
                chartDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()

    private fun startSpotifyImageWorker() {
        val request = OneTimeWorkRequestBuilder<SpotifyWorker>()
            .build()
        workManager.enqueueUniqueWork(
            GET_ARTIST_IMAGES_WORK,
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}
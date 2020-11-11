package de.schnettler.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.common.TimePeriod
import de.schnettler.database.AppDatabase
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListTrack
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.authentication.provider.LastFmAuthProvider
import de.schnettler.repo.mapping.album.TopUserAlbumMapper
import de.schnettler.repo.mapping.artist.TopUserArtistMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.mapping.forPagedLists
import de.schnettler.repo.mapping.track.TopUserTrackMapper
import de.schnettler.repo.paging.ChartRemoteMediator
import de.schnettler.repo.work.SpotifyWorker
import timber.log.Timber
import javax.inject.Inject

class TopListRepository @Inject constructor(
    private val userDao: UserDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val trackDao: TrackDao,
    private val chartDao: ChartDao,
    private val service: LastFmService,
    private val db: AppDatabase,
    private val authProvider: LastFmAuthProvider,
    private val workManager: WorkManager,
) {

    val topAlbumStore = StoreBuilder.from(
        fetcher = Fetcher.of { timePeriod: TimePeriod ->
            TopUserAlbumMapper.forLists()(
                service.getUserTopAlbums(timePeriod, authProvider.getSessionKeyOrThrow())
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
                service.getUserTopTracks(timePeriod, authProvider.getSessionKeyOrThrow())
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

    val pageSize = 5
    suspend fun userArtistPager(timePeriod: TimePeriod) = Pager(
        config = PagingConfig(pageSize, enablePlaceholders = true),
        remoteMediator = ChartRemoteMediator<LastFmEntity.Artist, TopListArtist>(
            pageSize = pageSize,
            fetcher = { page ->
                Timber.d("Paging Fetching $page for ${timePeriod.name}")
                val session = authProvider.getSessionOrThrow()
                val response = service.getUserTopArtists(page, pageSize, timePeriod, session.key)
                if (timePeriod == TimePeriod.OVERALL) {
                    userDao.updateArtistCount(session.name, response.info.total)
                }
                TopUserArtistMapper.forPagedLists(page = page, size = pageSize)(response.artist)
            },
            writer = { toplist, isRefresh ->
                db.withTransaction {
                    if (isRefresh) {
                    chartDao.deleteByType(EntityType.ARTIST, ListType.USER)
                    }
                    artistDao.insertAll(toplist.map { it.value })
                    chartDao.forceInsertAll(toplist.map { it.listing })
                }
                startSpotifyImageWorker()
            }
        )
    ) {
        chartDao.getTopArtistsPaging(listType = ListType.USER)
    }.flow
}
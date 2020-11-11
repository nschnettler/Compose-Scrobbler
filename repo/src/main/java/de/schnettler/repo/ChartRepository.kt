package de.schnettler.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.Toplist
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.artist.ChartArtistMapper
import de.schnettler.repo.mapping.artist.ChartTrackMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.paging.ChartRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChartRepository @Inject constructor(
    chartDao: ChartDao,
    artistDao: ArtistDao,
    trackDao: TrackDao,
    service: LastFmService
) {
    private val pageSize = 50

    val artistChartPager: Flow<PagingData<Toplist>> = Pager(
        config = PagingConfig(pageSize, enablePlaceholders = true),
        remoteMediator = ChartRemoteMediator<LastFmEntity.Artist, Toplist>(
            pageSize = pageSize,
            fetcher = { page -> ChartArtistMapper.forLists()(service.getTopArtists(page)) },
            writer = { toplist, isRefresh ->
                artistDao.insertAll(toplist.map { it.value as LastFmEntity.Artist })
                chartDao.forceInsertAll(toplist.map { it.listing })
            }
        )
    ) {
        chartDao.getTopArtistsPaging() as PagingSource<Int, Toplist>
    }.flow

    val trackChartPager = Pager(
        config = PagingConfig(pageSize, enablePlaceholders = true),
        remoteMediator = ChartRemoteMediator<LastFmEntity.Track, Toplist>(
            pageSize = pageSize,
            fetcher = { page -> ChartTrackMapper.forLists()(service.getTopTracks(page)) },
            writer = { toplist, isRefresh ->
                trackDao.insertAll(toplist.map { it.value as LastFmEntity.Track })
                chartDao.forceInsertAll(toplist.map { it.listing })
            }
        )
    ) {
        chartDao.getTopTracksPaging() as PagingSource<Int, Toplist>
    }.flow
}
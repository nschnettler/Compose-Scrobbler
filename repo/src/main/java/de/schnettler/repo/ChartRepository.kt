package de.schnettler.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.Toplist
import de.schnettler.lastfm.api.lastfm.ChartService
import de.schnettler.repo.mapping.artist.ChartArtistMapper
import de.schnettler.repo.mapping.artist.ChartTrackMapper
import de.schnettler.repo.paging.ChartRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val trackDao: TrackDao,
    private val service: ChartService
) {
    private val pageSize = 50

    val artistChartPager: Flow<PagingData<Toplist>> = Pager(
        config = PagingConfig(pageSize, initialLoadSize = pageSize),
        remoteMediator = ChartRemoteMediator(
            pageSize = pageSize,
            mapper = ChartArtistMapper,
            clear = { chartDao.clearTopList(EntityType.ARTIST, ListType.CHART) },
            insert = { topArtists ->
                artistDao.insertAll(topArtists.map { it.value as LastFmEntity.Artist })
                chartDao.forceInsertAll(topArtists.map { it.listing })
            },
            apiCall = service::getTopArtists
        )
    ) {
        chartDao.getTopArtistsPaging() as PagingSource<Int, Toplist>
    }.flow

    val trackChartPager: Flow<PagingData<Toplist>> = Pager(
        config = PagingConfig(pageSize, enablePlaceholders = true),
        remoteMediator = ChartRemoteMediator(
            pageSize = pageSize,
            mapper = ChartTrackMapper,
            clear = { chartDao.clearTopList(EntityType.TRACK, ListType.CHART) },
            insert = { topTracks ->
                trackDao.insertAll(topTracks.map { it.value as LastFmEntity.Track })
                chartDao.forceInsertAll(topTracks.map { it.listing })
            },
            apiCall = service::getTopTracks
        )
    ) {
        chartDao.getTopTracksPaging() as PagingSource<Int, Toplist>
    }.flow
}
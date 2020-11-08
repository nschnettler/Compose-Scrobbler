package de.schnettler.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.models.Toplist
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.artist.ChartArtistMapper
import de.schnettler.repo.mapping.artist.ChartTrackMapper
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
        remoteMediator = ChartRemoteMediator(
            pageSize = pageSize,
            entityDao = artistDao,
            chartDao = chartDao,
            mapper = ChartArtistMapper,
        ) { service.getTopArtists(it) }
    ) {
        chartDao.getTopArtistsPaging() as PagingSource<Int, Toplist>
    }.flow

    val trackChartPager = Pager(
        config = PagingConfig(pageSize, enablePlaceholders = true),
        remoteMediator = ChartRemoteMediator(
            pageSize = pageSize,
            entityDao = trackDao,
            chartDao = chartDao,
            mapper = ChartTrackMapper,
        ) { service.getTopTracks(it) }
    ) {
        chartDao.getTopTracksPaging() as PagingSource<Int, Toplist>
    }.flow
}
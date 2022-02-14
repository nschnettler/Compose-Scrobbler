package de.schnettler.scrobbler.charts.repo

import androidx.paging.ExperimentalPagingApi
import de.schnettler.scrobbler.charts.api.ChartApi
import de.schnettler.scrobbler.charts.dao.ChartDao
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import de.schnettler.scrobbler.persistence.dao.TrackDao
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val trackDao: TrackDao,
    private val chartApi: ChartApi,
) {
    private val pageSize = 50

//    val chartArtistPager: Flow<PagingData<TopListArtist>> = Pager(
//        config = PagingConfig(pageSize, initialLoadSize = pageSize),
//        remoteMediator = ChartRemoteMediator(
//            pageSize = pageSize,
//            mapper = ChartArtistMapper,
//            clear = chartDao::clearTopArtists,
//            insert = { topArtists ->
//                artistDao.insertAll(topArtists.map { it.value })
//                chartDao.forceInsertAll(topArtists.map { it.listing })
//            },
//            apiCall = chartApi::getTopArtists
//        )
//    ) {
//        chartDao.getTopArtists()
//    }.flow
//
//    val chartTrackPager: Flow<PagingData<TopListTrack>> = Pager(
//        config = PagingConfig(pageSize, enablePlaceholders = true),
//        remoteMediator = ChartRemoteMediator(
//            pageSize = pageSize,
//            mapper = ChartTrackMapper,
//            clear = chartDao::clearTopTracks,
//            insert = { topTracks ->
//                trackDao.insertAll(topTracks.map { it.value })
//                chartDao.forceInsertAll(topTracks.map { it.listing })
//            },
//            apiCall = chartApi::getTopTracks
//        )
//    ) {
//        chartDao.getTopTracks()
//    }.flow
}
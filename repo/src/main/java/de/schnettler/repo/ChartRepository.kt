package de.schnettler.repo

import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.lastfm.api.lastfm.LastFmService
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val service: LastFmService
) {
//    fun getArtistChart(): Flow<StoreResponse<List<Artist>>> {
//        val userInfoStore = StoreBuilder.from(
//            fetcher = Fetcher.of { _: String ->
//                service.getTopArtists().map { it.map() }
//            },
//            sourceOfTruth = SourceOfTruth.of(
//                reader = { _: String ->
//                    chartDao.getTopArtists(TopListEntryType.CHART_ARTIST)
//                        .map { list -> list.map { it.data } }
//                },
//                writer = { _: String, listings: List<Artist> ->
//                    val topListEntries = TopListMapper.forLists()
//                        .invoke(listings.map { Pair(it, TopListEntryType.CHART_ARTIST) })
//                    artistDao.insertEntitiesWithTopListEntries(
//                        listings,
//                        topListEntries
//                    )
//                }
//            )
//        ).build()
//        return userInfoStore.stream(StoreRequest.cached("", true))
//    }
}
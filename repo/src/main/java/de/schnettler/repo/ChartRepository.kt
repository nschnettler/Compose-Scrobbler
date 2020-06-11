package de.schnettler.repo

import com.dropbox.android.external.store4.*
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.models.Artist
import de.schnettler.database.models.TopListEntryType
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.TopListMapper
import de.schnettler.repo.mapping.forLists
import de.schnettler.repo.mapping.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val service: LastFmService
) {
    fun getArtistChart(): Flow<StoreResponse<List<Artist>>> {
        val userInfoStore = StoreBuilder.from(
            fetcher = nonFlowValueFetcher { _: String ->
                service.getTopArtists().map { it.map() }

            },
            sourceOfTruth = SourceOfTruth.from(
                reader = { _: String ->
                    chartDao.getTopArtists(TopListEntryType.CHART_ARTIST)
                        .map { list -> list.map { it.data } }
                },
                writer = { _: String, listings: List<Artist> ->
                    val topListEntries = TopListMapper.forLists()
                        .invoke(listings.map { Pair(it, TopListEntryType.CHART_ARTIST) })
                    artistDao.insertEntitiesWithTopListEntries(
                        listings,
                        topListEntries
                    )
                }
            )
        ).build()
        return userInfoStore.stream(StoreRequest.cached("", true))
    }
}
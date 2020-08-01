package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.TopListDao
import de.schnettler.database.models.ListType
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.ChartArtistMapper
import de.schnettler.repo.mapping.forLists
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val topListDao: TopListDao,
    private val service: LastFmService,
    private val artistMapper: ChartArtistMapper
) {
    val chartArtistsStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            artistMapper.forLists()(service.getTopArtists())
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopArtists(listType = ListType.CHART) },
            writer = { _: String, entries ->
                artistDao.insertAll(entries.map { it.value })
                topListDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()
}
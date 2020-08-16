package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.models.ListType
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.artist.ChartArtistMapper
import de.schnettler.repo.mapping.forLists
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val service: LastFmService
) {
    val chartArtistsStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            ChartArtistMapper.forLists()(service.getTopArtists())
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopArtists(listType = ListType.CHART) },
            writer = { _: String, entries ->
                artistDao.insertAll(entries.map { it.value })
                chartDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()
}
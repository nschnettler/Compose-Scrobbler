package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.ImageDao
import de.schnettler.lastfm.api.lastfm.ChartService
import de.schnettler.repo.mapping.artist.ChartArtistMapper
import de.schnettler.repo.mapping.artist.ChartTrackMapper
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val trackDao: ImageDao,
    private val service: ChartService
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

    val chartTrackStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            ChartTrackMapper.forLists()(service.getTopTracks())
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopTracks(listType = ListType.CHART) },
            writer = { _: String, entries ->
                trackDao.insertAll(entries.map { it.value })
                chartDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()
}
package de.schnettler.scrobbler.charts.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.scrobbler.charts.api.ChartApi
import de.schnettler.scrobbler.charts.dao.ChartDao
import de.schnettler.scrobbler.charts.map.ChartArtistMapper
import de.schnettler.scrobbler.charts.map.ChartTrackMapper
import de.schnettler.scrobbler.core.map.forLists
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import de.schnettler.scrobbler.persistence.dao.TrackDao
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val chartDao: ChartDao,
    private val artistDao: ArtistDao,
    private val trackDao: TrackDao,
    private val chartApi: ChartApi,
) {
    val chartArtistsStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            ChartArtistMapper.forLists()(chartApi.getTopArtists())
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopArtists() },
            writer = { _: String, entries ->
                artistDao.insertAll(entries.map { it.value })
                chartDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()

    val chartTrackStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            ChartTrackMapper.forLists()(chartApi.getTopTracks())
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { chartDao.getTopTracks() },
            writer = { _: String, entries ->
                trackDao.insertAll(entries.map { it.value })
                chartDao.forceInsertAll(entries.map { it.listing })
            }
        )
    ).build()
}
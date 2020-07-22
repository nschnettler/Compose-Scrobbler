package de.schnettler.repo

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.nonFlowValueFetcher
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.map
import de.schnettler.repo.mapping.mapToAlbum
import de.schnettler.repo.mapping.mapToArtist
import de.schnettler.repo.mapping.mapToTrack
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: LastFmService
) {
    val artistStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {query: String ->
            service.searchArtist(query, 3).map { it.mapToArtist() } +
                    service.searchAlbum(query, 3).map { it.mapToAlbum() } +
                    service.searchTrack(query, 3).map { it.mapToTrack() }
        }
    ).build()

    val albumStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {query: String ->
            service.searchAlbum(query, 5).map { it.mapToAlbum() }
        }
    ).build()
}
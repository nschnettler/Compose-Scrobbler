package de.schnettler.repo

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.nonFlowValueFetcher
import de.schnettler.lastfm.api.lastfm.LastFmService
import de.schnettler.repo.mapping.mapToAlbum
import de.schnettler.repo.mapping.mapToArtist
import de.schnettler.repo.mapping.mapToTrack
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: LastFmService
) {
    val artistStore = StoreBuilder.from(
        fetcher = nonFlowValueFetcher {query: SearchQuery ->
            when(query.filter) {
                1 -> service.searchArtist(query.query, 30).map { it.mapToArtist() }
                2 -> service.searchAlbum(query.query, 30).map { it.mapToAlbum() }
                3 -> service.searchTrack(query.query, 30).map { it.mapToTrack() }
                else -> service.searchArtist(query.query, 3).map { it.mapToArtist() } +
                        service.searchAlbum(query.query, 3).map { it.mapToAlbum() } +
                        service.searchTrack(query.query, 3).map { it.mapToTrack() }
            }
        }
    ).build()
}

data class SearchQuery(val query: String, val filter: Int)
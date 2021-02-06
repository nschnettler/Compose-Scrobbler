package de.schnettler.repo

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.StoreBuilder
import de.schnettler.lastfm.api.lastfm.SearchService
import de.schnettler.repo.mapping.search.mapToAlbum
import de.schnettler.repo.mapping.search.mapToArtist
import de.schnettler.repo.mapping.search.mapToTrack
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: SearchService
) {
    val artistStore = StoreBuilder.from(
        fetcher = Fetcher.of { query: SearchQuery ->
            when (query.filter) {
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